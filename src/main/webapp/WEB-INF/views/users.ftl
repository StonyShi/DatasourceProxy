<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Users</title>
</head>
<body>
    <ui>
        <#list users as v>
        <li>${v!}</li>
        </#list>
    </ui>
</body>
</html>