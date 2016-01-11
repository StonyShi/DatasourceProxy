<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>user</title>
</head>
<body>
    <li>
        <#if ext>
            <#list users as user>
                <ul>${user.id}</ul>
            </#list>
        </#if>
    </li>
</body>
</html>