<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>user</title>
</head>
<body>
    <li>
    <#if user?exists>
        <ul>${user!}</ul>
    </#if>
    </li>
</body>
</html>