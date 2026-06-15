package com.example.supermarket.service.impl;

import com.example.supermarket.entity.*;
import com.example.supermarket.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisServiceImpl.class);

    private final ProductService productService;
    private final PurchaseService purchaseService;
    private final EmployeeService employeeService;
    private final SupplierService supplierService;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.model}")
    private String model;

    @Value("${deepseek.api.max-tokens}")
    private int maxTokens;

    private static final String DEEPSEEK_URL = "https://api.deepseek.com/chat/completions";

    @Override
    public AnalysisResponse analyze(AnalysisRequest request) {
        // 1. 校验 API key
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("your-api-key-here")) {
            throw new IllegalStateException("AI 服务未配置，请联系管理员设置 DEEPSEEK_API_KEY");
        }

        // 2. 收集全部业务数据
        List<Product> products = productService.getAllProducts();
        List<PurchaseOrder> orders = purchaseService.getAllOrders();
        List<PurchaseDetail> details = purchaseService.getAllDetails();
        List<Employee> employees = employeeService.getAllEmployees();
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        // 3. 构建数据快照 + 预计算汇总
        String userMessage = buildDataSnapshot(products, orders, details, employees, suppliers, request.getFocusArea());

        // 4. 构建 system prompt
        String systemPrompt = buildSystemPrompt(request.getFocusArea());

        // 5. 调用 DeepSeek API
        return callDeepSeek(systemPrompt, userMessage);
    }

    // ==================== 数据快照构建 ====================

    private String buildDataSnapshot(List<Product> products, List<PurchaseOrder> orders,
                                     List<PurchaseDetail> details, List<Employee> employees,
                                     List<Supplier> suppliers, String focusArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 超市当前数据快照\n\n");

        appendProductSection(sb, products);
        appendSupplierSection(sb, suppliers);
        appendOrderSection(sb, orders);
        appendEmployeeSection(sb, employees);
        appendComputedSummaries(sb, products, details, employees, orders, suppliers);

        return sb.toString();
    }

    private void appendProductSection(StringBuilder sb, List<Product> products) {
        sb.append("### 商品清单 (").append(products.size()).append(" 种)\n\n");
        sb.append("| 编号 | 名称 | 单价 | 供应商编号 |\n");
        sb.append("|------|------|------|------------|\n");
        for (Product p : products) {
            sb.append("| ").append(escape(p.getPId()))
              .append(" | ").append(escape(p.getPName()))
              .append(" | ¥").append(p.getPPrice())
              .append(" | ").append(escape(p.getSId()))
              .append(" |\n");
        }
        sb.append("\n");
    }

    private void appendSupplierSection(StringBuilder sb, List<Supplier> suppliers) {
        sb.append("### 供应商清单 (").append(suppliers.size()).append(" 家)\n\n");
        sb.append("| 编号 | 名称 | 联系人 | 电话 |\n");
        sb.append("|------|------|--------|------|\n");
        for (Supplier s : suppliers) {
            sb.append("| ").append(escape(s.getSId()))
              .append(" | ").append(escape(s.getSName()))
              .append(" | ").append(escape(s.getSContactPerson()))
              .append(" | ").append(escape(s.getSPhone()))
              .append(" |\n");
        }
        sb.append("\n");
    }

    private void appendOrderSection(StringBuilder sb, List<PurchaseOrder> orders) {
        sb.append("### 采购订单 (").append(orders.size()).append(" 单)\n\n");
        if (!orders.isEmpty()) {
            sb.append("| 单号 | 员工 | 数量 | 金额 | 时间 |\n");
            sb.append("|------|------|------|------|------|\n");
            for (PurchaseOrder o : orders) {
                sb.append("| ").append(escape(o.getOId()))
                  .append(" | ").append(escape(o.getEId()))
                  .append(" | ").append(o.getOTotalQuantity())
                  .append(" | ¥").append(o.getOTotalPrice())
                  .append(" | ").append(o.getOTime())
                  .append(" |\n");
            }
        }
        sb.append("\n");
    }

    private void appendEmployeeSection(StringBuilder sb, List<Employee> employees) {
        sb.append("### 员工清单 (").append(employees.size()).append(" 人)\n\n");
        sb.append("| 工号 | 姓名 | 级别 |\n");
        sb.append("|------|------|------|\n");
        for (Employee e : employees) {
            sb.append("| ").append(escape(e.getEId()))
              .append(" | ").append(escape(e.getEName()))
              .append(" | ").append(escape(e.getELevel()))
              .append(" |\n");
        }
        sb.append("\n");
    }

    private void appendComputedSummaries(StringBuilder sb, List<Product> products,
                                          List<PurchaseDetail> details,
                                          List<Employee> employees, List<PurchaseOrder> orders,
                                          List<Supplier> suppliers) {
        // 商品采购汇总
        Map<String, Integer> productQty = new LinkedHashMap<>();
        Map<String, BigDecimal> productAmount = new LinkedHashMap<>();
        Map<String, String> productName = new HashMap<>();
        Map<String, String> productSupplier = new HashMap<>();
        for (Product p : products) {
            productName.put(p.getPId(), p.getPName());
            productSupplier.put(p.getPId(), p.getSId());
            productQty.put(p.getPId(), 0);
            productAmount.put(p.getPId(), BigDecimal.ZERO);
        }
        for (PurchaseDetail d : details) {
            String pid = d.getPId();
            Integer qty = d.getDQuantity() != null ? d.getDQuantity() : 0;
            productQty.merge(pid, qty, Integer::sum);
            BigDecimal amt = BigDecimal.valueOf(qty)
                    .multiply(d.getDPrice() != null ? d.getDPrice() : BigDecimal.ZERO);
            productAmount.merge(pid, amt, BigDecimal::add);
        }

        sb.append("### 📊 商品采购量排名 (Top 15)\n\n");
        sb.append("| 排名 | 商品 | 供应商 | 采购总量 | 总金额 |\n");
        sb.append("|------|------|--------|----------|--------|\n");
        int rank = 1;
        var sorted = productQty.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(15).toList();
        for (var e : sorted) {
            sb.append("| ").append(rank++)
              .append(" | ").append(escape(productName.get(e.getKey())))
              .append(" | ").append(escape(productSupplier.get(e.getKey())))
              .append(" | ").append(e.getValue())
              .append(" | ¥").append(productAmount.get(e.getKey()))
              .append(" |\n");
        }
        sb.append("\n");

        // 库存预警：采购量低于中位数的商品
        if (!sorted.isEmpty()) {
            List<Integer> qtys = sorted.stream().map(Map.Entry::getValue).sorted().toList();
            int median = qtys.get(qtys.size() / 2);
            sb.append("### ⚠️ 库存预警参考\n\n");
            sb.append("采购量中位数: **").append(median).append(" 件**，低于此值的商品值得关注:\n\n");
            var low = sorted.stream().filter(en -> en.getValue() < median).toList();
            if (!low.isEmpty()) {
                for (var en : low) {
                    sb.append("- ").append(escape(productName.get(en.getKey())))
                      .append(" — 仅采购 ").append(en.getValue()).append(" 件\n");
                }
            } else {
                sb.append("所有商品采购量均衡\n");
            }
            sb.append("\n");
        }

        // 员工采购贡献
        Map<String, Integer> empOrderCount = new HashMap<>();
        Map<String, BigDecimal> empAmount = new HashMap<>();
        Map<String, String> empName = new HashMap<>();
        for (Employee emp : employees) {
            empName.put(emp.getEId(), emp.getEName());
            empOrderCount.put(emp.getEId(), 0);
            empAmount.put(emp.getEId(), BigDecimal.ZERO);
        }
        for (PurchaseOrder o : orders) {
            empOrderCount.merge(o.getEId(), 1, Integer::sum);
            empAmount.merge(o.getEId(),
                    o.getOTotalPrice() != null ? o.getOTotalPrice() : BigDecimal.ZERO,
                    BigDecimal::add);
        }
        sb.append("### 📊 员工采购贡献排名\n\n");
        sb.append("| 排名 | 员工 | 订单数 | 采购总额 |\n");
        sb.append("|------|------|--------|----------|\n");
        rank = 1;
        var empSorted = empAmount.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .toList();
        for (var e : empSorted) {
            sb.append("| ").append(rank++)
              .append(" | ").append(escape(empName.get(e.getKey())))
              .append(" | ").append(empOrderCount.get(e.getKey()))
              .append(" | ¥").append(e.getValue())
              .append(" |\n");
        }
        sb.append("\n");

        // 供应商供货汇总
        Map<String, Set<String>> supProducts = new HashMap<>();
        for (Product p : products) {
            supProducts.computeIfAbsent(p.getSId(), k -> new HashSet<>()).add(p.getPId());
        }
        Map<String, Integer> supQty = new HashMap<>();
        Map<String, String> supName = new HashMap<>();
        for (Supplier s : suppliers) {
            supName.put(s.getSId(), s.getSName());
            supQty.put(s.getSId(), 0);
        }
        for (PurchaseDetail d : details) {
            String sid = productSupplier.get(d.getPId());
            if (sid != null) {
                supQty.merge(sid, d.getDQuantity() != null ? d.getDQuantity() : 0, Integer::sum);
            }
        }
        sb.append("### 📊 供应商供货量排名\n\n");
        sb.append("| 排名 | 供应商 | 商品种类 | 供货总量 |\n");
        sb.append("|------|--------|----------|----------|\n");
        rank = 1;
        var supSorted = supQty.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();
        for (var e : supSorted) {
            String sid = e.getKey();
            sb.append("| ").append(rank++)
              .append(" | ").append(escape(supName.get(sid)))
              .append(" | ").append(supProducts.getOrDefault(sid, Set.of()).size())
              .append(" | ").append(e.getValue())
              .append(" |\n");
        }
        sb.append("\n");
    }

    // ==================== Prompt 构建 ====================

    private String buildSystemPrompt(String focusArea) {
        String base = "你是一位专业的超市进销存数据分析师。请根据提供的真实数据，生成一份详细的经营分析报告。";
        String format = "使用中文撰写，Markdown 格式，适当使用 emoji 图标增强可读性。";

        String dimensions;
        if (focusArea == null || focusArea.isBlank()) {
            dimensions = """
                请覆盖以下 4 个维度：
                1. 📦 商品采购分析 — 采购量排名、热门商品、单价分析、采购趋势
                2. 🏭 供应商分析 — 供货量排名、占比分析、主要供应商品
                3. ⚠️ 库存预警与补货建议 — 低库存商品识别、补货优先级建议
                4. 👤 员工绩效分析 — 采购贡献排名、活跃度分析

                每个维度请提供：
                - 关键发现（引用具体数字）
                - 数据驱动的建议（可执行的操作建议）
                """;
        } else {
            dimensions = "请重点分析「" + focusArea + "」维度，给出深入的数据洞察和可执行建议。";
        }

        return base + "\n" + format + "\n" + dimensions;
    }

    // ==================== DeepSeek API 调用 ====================

    private AnalysisResponse callDeepSeek(String systemPrompt, String userMessage) {
        // 设置 120 秒连接/读取超时，AI 分析需要较长时间
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(30));
        requestFactory.setReadTimeout(Duration.ofSeconds(120));
        RestClient client = RestClient.builder()
                .requestFactory(requestFactory)
                .build();

        // DeepSeek 用 OpenAI 兼容的 Chat Completions 格式
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("max_tokens", maxTokens);
        body.put("messages", messages);

        log.info("调用 DeepSeek API, model={}, maxTokens={}", model, maxTokens);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = client.post()
                    .uri(DEEPSEEK_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        byte[] bytes = res.getBody().readAllBytes();
                        String errorBody = new String(bytes);
                        log.error("DeepSeek API 错误: status={}, body={}", res.getStatusCode(), errorBody);
                        if (res.getStatusCode().value() == 401) {
                            throw new HttpClientErrorException(res.getStatusCode(),
                                    "AI 服务认证失败，请检查 API Key 配置");
                        } else if (res.getStatusCode().value() == 429) {
                            throw new HttpClientErrorException(res.getStatusCode(),
                                    "AI 请求过于频繁，请稍后再试");
                        } else if (res.getStatusCode().value() == 402) {
                            throw new HttpClientErrorException(res.getStatusCode(),
                                    "AI 服务账户余额不足，请联系管理员充值");
                        }
                        throw new HttpClientErrorException(res.getStatusCode(),
                                "AI 服务异常: " + res.getStatusCode());
                    })
                    .body(Map.class);

            // 提取响应文本（OpenAI 格式）
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            StringBuilder textBuilder = new StringBuilder();
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    textBuilder.append(message.get("content"));
                }
            }

            // 提取 usage
            @SuppressWarnings("unchecked")
            Map<String, Object> usage = (Map<String, Object>) response.get("usage");
            int inputTokens = 0;
            int outputTokens = 0;
            if (usage != null) {
                inputTokens = ((Number) usage.getOrDefault("prompt_tokens", 0)).intValue();
                outputTokens = ((Number) usage.getOrDefault("completion_tokens", 0)).intValue();
            }

            AnalysisResponse result = new AnalysisResponse();
            result.setContent(textBuilder.toString());
            result.setModel((String) response.get("model"));
            result.setInputTokens(inputTokens);
            result.setOutputTokens(outputTokens);
            log.info("DeepSeek API 调用成功, model={}, inputTokens={}, outputTokens={}",
                    result.getModel(), inputTokens, outputTokens);
            return result;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("DeepSeek API HTTP错误", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ==================== 工具方法 ====================

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|").replace("\n", " ");
    }
}
