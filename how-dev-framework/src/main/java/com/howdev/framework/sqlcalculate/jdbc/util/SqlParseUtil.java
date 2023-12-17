package com.howdev.framework.sqlcalculate.jdbc.util;

import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.validate.SqlConformanceEnum;

import java.util.HashSet;
import java.util.Set;

/**
 * SqlParseUtil class
 *
 * @author haozhifeng
 * @date 2023/12/11
 */
public class SqlParseUtil {

    /**
     * 从Sql中提取涉及的表名
     *
     * @param sql sql
     *
     * @return:
     * @author: haozhifeng
     */
    public static Set<String> getTableNamesOfSql(String sql) {

        SqlNode sqlNode = parseStatement(sql);
        return extractSourceTableInSelectSql(sqlNode, false);
    }

    public static SqlNode parseStatement(String sql) {
        SqlParser.ConfigBuilder configBuilder = SqlParser.configBuilder();
        configBuilder.setParserFactory(SqlParserImpl.FACTORY);
        configBuilder.setLex(Lex.MYSQL);
        // 设置一致性级别为LENIENT，这将允许更多的语法
        configBuilder.setConformance(SqlConformanceEnum.MYSQL_5);
        SqlParser.Config config = configBuilder.build();

        SqlParser parser = SqlParser.create(sql, config);
        try {
            SqlNode sqlNode = parser.parseQuery();
            return sqlNode;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("operation not allowed");
        }
    }

    private static Set<String> extractSourceTableInSelectSql(SqlNode sqlNode, boolean fromOrJoin) {
        if (sqlNode == null) {
            return new HashSet<>();
        }
        final SqlKind sqlKind = sqlNode.getKind();
        if (SqlKind.SELECT.equals(sqlKind)) {
            SqlSelect selectNode = (SqlSelect) sqlNode;
            Set<String> selectList = new HashSet<>(extractSourceTableInSelectSql(selectNode.getFrom(), true));
            selectNode.getSelectList().getList().stream().filter(node -> node instanceof SqlCall)
                    .forEach(node -> selectList.addAll(extractSourceTableInSelectSql(node, false)));
            selectList.addAll(extractSourceTableInSelectSql(selectNode.getWhere(), false));
            selectList.addAll(extractSourceTableInSelectSql(selectNode.getHaving(), false));
            return selectList;
        } else if (SqlKind.JOIN.equals(sqlKind)) {
            SqlJoin sqlJoin = (SqlJoin) sqlNode;
            Set<String> joinList = new HashSet<>();
            joinList.addAll(extractSourceTableInSelectSql(sqlJoin.getLeft(), true));
            joinList.addAll(extractSourceTableInSelectSql(sqlJoin.getRight(), true));
            return joinList;
        } else if (SqlKind.AS.equals(sqlKind)) {
            SqlCall sqlCall = (SqlCall) sqlNode;
            return extractSourceTableInSelectSql(sqlCall.getOperandList().get(0), fromOrJoin);
        } else if (SqlKind.IDENTIFIER.equals(sqlKind)) {
            Set<String> identifierList = new HashSet<>();
            if (fromOrJoin) {
                SqlIdentifier sqlIdentifier = (SqlIdentifier) sqlNode;
                identifierList.add(sqlIdentifier.toString());
            }
            return identifierList;
        } else {
            Set<String> defaultList = new HashSet<>();
            if (sqlNode instanceof SqlCall) {
                SqlCall call = (SqlCall) sqlNode;
                call.getOperandList()
                        .forEach(node -> defaultList.addAll(extractSourceTableInSelectSql(node, false)));
                return defaultList;
            }
            return defaultList;
        }
    }

    public static String cleanTable(String sql) {
        Set<String> tables = SqlParseUtil.getTableNamesOfSql(sql);
        String realSql = sql;
        for (String table : tables) {
            if (table.contains(".")) {
                String[] splitTables = table.split("\\.");
                realSql = realSql.replaceAll(table, splitTables[splitTables.length - 1]);
            }
        }
        return realSql;
    }

    public static void main(String[] args) throws Exception {

        String sql0 = "select sum(b) as dd , b.c from db.d where e = x and f not in (x,d)";

        String sql1 =
                "SELECT sum(x.dd) as xx ,2 from db.a x where id = xx and c = 'zz' group by xx order by dd limit 10 ";
        String sql2 = "SELECT sum(x.dd) as xx ,2 from db.a x where id = xx and c = 'zz' order by dd limit 10 ";
        String sql3 = "SELECT sum(f) as xx,e FROM db.B left join B.dd on dd.xx=b.cc WHERE g = h";
        String sql4 =
                "SELECT sum(f) as xx,e FROM db.B left join (select xx from B.dd union select xx from d.dddddd) as bdd"
                        + " on dd.xx=b.cc WHERE g = h";
        String sql5 = "SELECT sum(x.dd) as xx ,2 from db.a x where id = xx and c = 'zz'  " +
                "union all SELECT sum(f) as xx,e FROM db.B left join B.dd on dd.xx=b.cc WHERE g = h limit 10,10";

        String sql6 = "SELECT a, b FROM table1 JOIN table2 ON table1.id = table2.id WHERE c = (SELECT d FROM table3)";

        String sql7 = "select max(cast(usedAmount as double)/cast(totalAmount as double))"
                + " as amountMaxUseRate from UserAmountInfo.AmountData";

        String sql8 = "select sum(testFunc(orderDetailAmount,orderDetailStatus,"
                + "(select orderTime from OrderInfo))) as last1MonthValidConsumerAmt from OrderDetailInfo";

        String sql9 = "SELECT MIN(relation_id) FROM tableA JOIN TableB  GROUP BY account_instance_id, "
                + "follow_account_instance_id HAVING COUNT(*)>1";

        String sql10 = "SELECT * FROM blog_user_relation a WHERE (a.account_instance_id,a.follow_account_instance_id)"
                + " IN (SELECT account_instance_id,follow_account_instance_id FROM Blogs_info GROUP BY account_instance_id, follow_account_instance_id HAVING COUNT(*) > 1)";
        String sql11 = "select name from (select * from student)";
        String sql12 = "SELECT * FROM Student LEFT JOIN Grade ON Student.sID = Grade.gID\n" +
                "UNION\n" +
                "SELECT * FROM Student RIGHT JOIN Grade ON Student.sID = Grade.gID";
        String sql13 = "SELECT *\n" +
                "FROM teacher\n" +
                "WHERE birth = (SELECT MIN(birth)\n" +
                "               FROM employee)";
        String sql14 = "SELECT sName\n" +
                "FROM Student\n" +
                "WHERE '450' NOT IN (SELECT courseID\n" +
                "                    FROM Course\n" +
                "                    WHERE sID = Student.sID)";

        System.out.println(getTableNamesOfSql(sql14));

    }
}