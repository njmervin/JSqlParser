# SQL Parser for Java

## Use in maven
```xml
<dependency>
    <groupId>org.yuyun</groupId>
    <artifactId>JSqlParser</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Sample
```java
Reader reader = new InputStreamReader(Files.newInputStream(Paths.get("Z:\\union09.sql")), StandardCharsets.UTF_8);
char[] cbuf = new char[128*1024];
int n = reader.read(cbuf);
SQLParser parser = SQLParser.parse(new String(cbuf, 0, n));
for(SQLStmt stmt : parser.getStmtList()) {
    System.out.printf("===> line: %d%n", stmt.getFirstLine());
    System.out.println(stmt.getPrimaryOperation());
    System.out.println(stmt.getFromTables());
    System.out.println(stmt.getAlterOperation());
    System.out.println(stmt.getSql());
    System.out.println(stmt.getCleanSQL());
}
```
