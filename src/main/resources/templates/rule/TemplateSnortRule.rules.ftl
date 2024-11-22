<#list snortRuleList as snortRule>
alert ${snortRule.protocol} any any -> ${snortRule.destination} any (msg:"${snortRule.msg}"; sid:${snortRule.sid}; rev:${snortRule.rev};)
</#list>
