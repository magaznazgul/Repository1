import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    private JComboBox<String> tableCombo;
    private JList<String> columnList;
    private DefaultListModel<String> columnModel;

    private JPanel conditionsPanel;
    private List<ConditionRow> conditionRows;

    private JComboBox<String> orderColumnCombo;
    private JComboBox<String> orderDirectionCombo;

    private JCheckBox limitCheckBox;
    private JTextField limitField;

    private JTextArea resultArea;

    private final Map<String, List<String>> tableColumns =
            new LinkedHashMap<>();

    public Main() {

        setTitle("SQL Query Builder - Builder Pattern");
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeTableColumns();
        conditionRows = new ArrayList<>();

        createInterface();
    }

    // Таблицалар мен олардың бағандарын анықтау
    private void initializeTableColumns() {

        tableColumns.put("users", Arrays.asList(
                "id", "name", "email", "age", "status"
        ));

        tableColumns.put("products", Arrays.asList(
                "id", "name", "price", "status",
                "category", "description"
        ));

        tableColumns.put("orders", Arrays.asList(
                "id", "user_id", "product_id",
                "price", "status", "created_at"
        ));
    }

    // Негізгі интерфейсті құру
    private void createInterface() {

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(235, 250, 248));

        JLabel title = new JLabel("SQL QUERY BUILDER");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(30, 100, 100));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(235, 250, 248));
        header.add(title);

        mainPanel.add(header, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(235, 250, 248));

        // TABLE
        JPanel tablePanel = createSection("1. SELECT TABLE");

        tableCombo = new JComboBox<>(
                tableColumns.keySet().toArray(new String[0])
        );

        tableCombo.setPreferredSize(new Dimension(250, 30));

        tablePanel.add(tableCombo);

        // COLUMNS
        JPanel columnsPanel = createSection("2. SELECT COLUMNS");

        columnModel = new DefaultListModel<>();
        columnList = new JList<>(columnModel);

        columnList.setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        );

        columnList.setVisibleRowCount(4);

        JScrollPane columnScroll = new JScrollPane(columnList);
        columnScroll.setPreferredSize(new Dimension(250, 90));

        columnsPanel.add(columnScroll);

        updateColumns();

        tableCombo.addActionListener(e -> updateColumns());

        // CONDITIONS
        JPanel wherePanel = createSection("3. WHERE CONDITIONS");

        conditionsPanel = new JPanel();
        conditionsPanel.setLayout(
                new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS)
        );
        conditionsPanel.setBackground(Color.WHITE);

        JButton addConditionButton = new JButton("+ Add Condition");
        JButton removeConditionButton = new JButton("- Remove Condition");

        addConditionButton.addActionListener(e -> addConditionRow());

        removeConditionButton.addActionListener(e -> removeConditionRow());

        JPanel conditionButtons = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        conditionButtons.setBackground(Color.WHITE);
        conditionButtons.add(addConditionButton);
        conditionButtons.add(removeConditionButton);

        wherePanel.setLayout(new BorderLayout(5, 5));
        wherePanel.add(conditionsPanel, BorderLayout.CENTER);
        wherePanel.add(conditionButtons, BorderLayout.SOUTH);

        addConditionRow();

        // ORDER BY
        JPanel orderPanel = createSection("4. ORDER BY");

        orderColumnCombo = new JComboBox<>();
        orderDirectionCombo = new JComboBox<>(
                new String[]{"ASC", "DESC"}
        );

        orderColumnCombo.addItem("None");

        orderPanel.add(new JLabel("Column:"));
        orderPanel.add(orderColumnCombo);

        orderPanel.add(new JLabel("Direction:"));
        orderPanel.add(orderDirectionCombo);

        // LIMIT
        JPanel limitPanel = createSection("5. LIMIT");

        limitCheckBox = new JCheckBox("Enable LIMIT");
        limitField = new JTextField("10", 8);

        limitCheckBox.setBackground(Color.WHITE);

        limitPanel.add(limitCheckBox);
        limitPanel.add(limitField);

        // BUTTONS
        JPanel buttonsPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        buttonsPanel.setBackground(new Color(235, 250, 248));

        JButton generateButton = new JButton("Generate SQL");
        JButton clearButton = new JButton("Clear All");
        JButton copyButton = new JButton("Copy SQL");

        generateButton.addActionListener(e -> generateQuery());

        clearButton.addActionListener(e -> clearAll());

        copyButton.addActionListener(e -> copyResult());

        buttonsPanel.add(generateButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(copyButton);

        // RESULT
        JPanel resultPanel = createSection("6. GENERATED SQL QUERY");

        resultArea = new JTextArea(12, 70);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 250, 250));

        JScrollPane resultScroll = new JScrollPane(resultArea);

        resultPanel.setLayout(new BorderLayout());
        resultPanel.add(resultScroll, BorderLayout.CENTER);

        contentPanel.add(tablePanel);
        contentPanel.add(columnsPanel);
        contentPanel.add(wherePanel);
        contentPanel.add(orderPanel);
        contentPanel.add(limitPanel);
        contentPanel.add(buttonsPanel);
        contentPanel.add(resultPanel);

        JScrollPane mainScroll = new JScrollPane(contentPanel);
        mainScroll.setBorder(null);

        mainPanel.add(mainScroll, BorderLayout.CENTER);

        add(mainPanel);
    }

    // Интерфейс бөлімін құру
    private JPanel createSection(String title) {

        JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(
                        new Color(150, 200, 195)
                ),
                title
        ));

        panel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 180)
        );

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    // Таңдалған кестеге сәйкес бағандарды жаңарту
    private void updateColumns() {

        String table = (String) tableCombo.getSelectedItem();

        if (table == null) {
            return;
        }

        columnModel.clear();

        List<String> columns = tableColumns.get(table);

        for (String column : columns) {
            columnModel.addElement(column);
        }

        updateConditionColumns();
        updateOrderColumns();
    }

    // WHERE бағандарын жаңарту
    private void updateConditionColumns() {

        for (ConditionRow row : conditionRows) {
            row.updateColumns();
        }
    }

    // ORDER BY бағандарын жаңарту
    private void updateOrderColumns() {

        if (orderColumnCombo == null) {
            return;
        }

        orderColumnCombo.removeAllItems();
        orderColumnCombo.addItem("None");

        String table = (String) tableCombo.getSelectedItem();

        if (table != null) {

            for (String column : tableColumns.get(table)) {
                orderColumnCombo.addItem(column);
            }
        }
    }

    // Жаңа WHERE шартын қосу
    private void addConditionRow() {

        ConditionRow row = new ConditionRow();

        conditionRows.add(row);

        conditionsPanel.add(row.panel);

        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    // Соңғы WHERE шартын жою
    private void removeConditionRow() {

        if (conditionRows.size() > 0) {

            ConditionRow row =
                    conditionRows.remove(conditionRows.size() - 1);

            conditionsPanel.remove(row.panel);

            conditionsPanel.revalidate();
            conditionsPanel.repaint();
        }
    }

    // SQL сұранысын құрастыру
    private void generateQuery() {

        try {

            String table = (String) tableCombo.getSelectedItem();

            if (table == null) {
                throw new IllegalArgumentException(
                        "Please select a table."
                );
            }

            List<String> selectedColumns =
                    columnList.getSelectedValuesList();

            // Builder объектісін құру
            SQLQueryBuilder builder = new SQLQueryBuilder();

            // МАҢЫЗДЫ: алдымен FROM, содан кейін SELECT
            builder.from(table);

            if (selectedColumns.isEmpty()) {
                builder.select();
            } else {
                builder.select(
                        selectedColumns.toArray(new String[0])
                );
            }

            // WHERE шарттарын қосу
            for (int i = 0; i < conditionRows.size(); i++) {

                ConditionRow row = conditionRows.get(i);

                String column =
                        (String) row.columnCombo.getSelectedItem();

                String operator =
                        (String) row.operatorCombo.getSelectedItem();

                String value = row.valueField.getText().trim();

                String logic =
                        (String) row.logicCombo.getSelectedItem();

                if (column == null || value.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Please fill in all WHERE conditions."
                    );
                }

                if (i == 0) {
                    builder.where(column, operator, value);
                } else {
                    builder.where(
                            column, operator, value, logic
                    );
                }
            }

            // ORDER BY
            String orderColumn =
                    (String) orderColumnCombo.getSelectedItem();

            if (orderColumn != null &&
                    !orderColumn.equals("None")) {

                String direction =
                        (String) orderDirectionCombo.getSelectedItem();

                builder.orderBy(orderColumn, direction);
            }

            // LIMIT
            if (limitCheckBox.isSelected()) {

                int limit = Integer.parseInt(
                        limitField.getText().trim()
                );

                builder.limit(limit);
            }

            // SQL сұранысын алу
            String sql = builder.build();

            // SQL және параметрлерді бірге көрсету
            StringBuilder output = new StringBuilder();

            output.append("GENERATED SQL QUERY\n\n");
            output.append(sql);
            output.append("\n\n");

            output.append("QUERY PARAMETERS:\n");

            List<Object> parameters = builder.getParameters();

            if (parameters.isEmpty()) {

                output.append("No parameters");

            } else {

                for (int i = 0; i < parameters.size(); i++) {

                    output.append(i + 1)
                            .append(". ")
                            .append(parameters.get(i))
                            .append("\n");
                }
            }

            resultArea.setText(output.toString());

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "LIMIT must be a number!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (IllegalArgumentException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Query Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Барлық өрістерді тазалау
    private void clearAll() {

        columnList.clearSelection();

        conditionRows.clear();
        conditionsPanel.removeAll();

        addConditionRow();

        orderColumnCombo.setSelectedIndex(0);

        orderDirectionCombo.setSelectedIndex(0);

        limitCheckBox.setSelected(false);
        limitField.setText("10");

        resultArea.setText("");

        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    // Нәтижені көшіру
    private void copyResult() {

        String text = resultArea.getText();

        if (!text.isEmpty()) {

            StringSelection selection =
                    new StringSelection(text);

            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(selection, null);

            JOptionPane.showMessageDialog(
                    this,
                    "Result copied successfully!"
            );
        }
    }

    // WHERE шартының интерфейсі
    class ConditionRow {

        JPanel panel;

        JComboBox<String> columnCombo;
        JComboBox<String> operatorCombo;
        JTextField valueField;
        JComboBox<String> logicCombo;

        ConditionRow() {

            panel = new JPanel(
                    new FlowLayout(FlowLayout.LEFT, 5, 5)
            );

            panel.setBackground(Color.WHITE);

            columnCombo = new JComboBox<>();

            operatorCombo = new JComboBox<>(
                    new String[]{
                            "=", "!=", ">", "<",
                            ">=", "<=", "LIKE"
                    }
            );

            valueField = new JTextField(10);

            logicCombo = new JComboBox<>(
                    new String[]{"AND", "OR"}
            );

            panel.add(new JLabel("Column:"));
            panel.add(columnCombo);

            panel.add(new JLabel("Operator:"));
            panel.add(operatorCombo);

            panel.add(new JLabel("Value:"));
            panel.add(valueField);

            panel.add(logicCombo);

            updateColumns();
        }

        void updateColumns() {

            columnCombo.removeAllItems();

            String table =
                    (String) tableCombo.getSelectedItem();

            if (table != null) {

                for (String column : tableColumns.get(table)) {
                    columnCombo.addItem(column);
                }
            }
        }
    }

    // Builder интерфейсі
    interface QueryBuilder {

        QueryBuilder from(String table);

        QueryBuilder select(String... columns);

        QueryBuilder where(
                String column,
                String operator,
                Object value
        );

        QueryBuilder where(
                String column,
                String operator,
                Object value,
                String logic
        );

        QueryBuilder orderBy(
                String column,
                String direction
        );

        QueryBuilder limit(int limit);

        String build();
    }

    // SQL Query Builder класы
    static class SQLQueryBuilder implements QueryBuilder {

        private String table;

        private List<String> selectedColumns =
                new ArrayList<>();

        private List<Condition> conditions =
                new ArrayList<>();

        private String orderColumn;

        private String orderDirection;

        private Integer limitValue;

        private List<Object> parameters =
                new ArrayList<>();

        // Рұқсат етілген кестелер мен бағандар
        private static final Map<String, List<String>>
                ALLOWED_COLUMNS = new HashMap<>();

        static {

            ALLOWED_COLUMNS.put("users", Arrays.asList(
                    "id", "name", "email", "age", "status"
            ));

            ALLOWED_COLUMNS.put("products", Arrays.asList(
                    "id", "name", "price", "status",
                    "category", "description"
            ));

            ALLOWED_COLUMNS.put("orders", Arrays.asList(
                    "id", "user_id", "product_id",
                    "price", "status", "created_at"
            ));
        }

        // Кестені таңдау
        @Override
        public QueryBuilder from(String table) {

            if (!ALLOWED_COLUMNS.containsKey(table)) {
                throw new IllegalArgumentException(
                        "Invalid table: " + table
                );
            }

            this.table = table;

            return this;
        }

        // Бағанды тексеру
        private void checkColumn(String column) {

            if (table == null) {
                throw new IllegalArgumentException(
                        "Please select table first."
                );
            }

            if (!ALLOWED_COLUMNS.get(table).contains(column)) {

                throw new IllegalArgumentException(
                        "Invalid column: " + column
                );
            }
        }

        // SELECT
        @Override
        public QueryBuilder select(String... columns) {

            selectedColumns.clear();

            if (columns == null || columns.length == 0) {
                return this;
            }

            for (String column : columns) {

                checkColumn(column);

                selectedColumns.add(column);
            }

            return this;
        }

        // WHERE
        @Override
        public QueryBuilder where(
                String column,
                String operator,
                Object value
        ) {

            checkColumn(column);

            conditions.add(
                    new Condition(column, operator, value, "AND")
            );

            return this;
        }

        @Override
        public QueryBuilder where(
                String column,
                String operator,
                Object value,
                String logic
        ) {

            checkColumn(column);

            if (!logic.equals("AND") && !logic.equals("OR")) {

                throw new IllegalArgumentException(
                        "Invalid logical operator: " + logic
                );
            }

            conditions.add(
                    new Condition(column, operator, value, logic)
            );

            return this;
        }

        // ORDER BY
        @Override
        public QueryBuilder orderBy(
                String column,
                String direction
        ) {

            checkColumn(column);

            if (!direction.equals("ASC") &&
                    !direction.equals("DESC")) {

                throw new IllegalArgumentException(
                        "Invalid sort direction."
                );
            }

            this.orderColumn = column;
            this.orderDirection = direction;

            return this;
        }

        // LIMIT
        @Override
        public QueryBuilder limit(int limit) {

            if (limit <= 0) {

                throw new IllegalArgumentException(
                        "LIMIT must be greater than zero."
                );
            }

            this.limitValue = limit;

            return this;
        }

        // SQL сұранысын құрастыру
        @Override
        public String build() {

            if (table == null) {
                throw new IllegalArgumentException(
                        "Table is not selected."
                );
            }

            StringBuilder sql = new StringBuilder();

            parameters.clear();

            sql.append("SELECT ");

            if (selectedColumns.isEmpty()) {

                sql.append("*");

            } else {

                sql.append(
                        String.join(", ", selectedColumns)
                );
            }

           sql.append("\nFROM ").append(table);

            if (!conditions.isEmpty()) {

                sql.append("\nWHERE ");

                for (int i = 0; i < conditions.size(); i++) {

                    Condition condition = conditions.get(i);

                    if (i > 0) {
                        sql.append(" ")
                                .append(condition.logic)
                                .append(" ");
                    }

                    sql.append(condition.column)
                            .append(" ")
                            .append(condition.operator)
                            .append(" ?");

                    parameters.add(condition.value);
                }
            }

            if (orderColumn != null) {

                sql.append("\nORDER BY ")
                        .append(orderColumn)
                        .append(" ")
                        .append(orderDirection);
            }

            if (limitValue != null) {

                sql.append("\nLIMIT ")
                        .append(limitValue);
            }

            sql.append(";");

            return sql.toString();
        }

        // Параметрлер тізімін алу
        public List<Object> getParameters() {

            return new ArrayList<>(parameters);
        }
    }

    // WHERE шартының деректер класы
    static class Condition {

        String column;
        String operator;
        Object value;
        String logic;

        Condition(
                String column,
                String operator,
                Object value,
                String logic
        ) {

            this.column = column;
            this.operator = operator;
            this.value = value;
            this.logic = logic;
        }
    }

    // Бағдарламаны іске қосу
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Main application = new Main();

            application.setVisible(true);
        });
    }
}