rule ${ruleName} {
    meta:
        description = "${description}"
        reference = "${reference}"
        author = "${author}"
        date = "${createDate}"
    strings:
    <#list variableList as variable>
        ${variable.variableName} = "${variable.variableValue}"
    </#list>
    condition:
        ${condition}
}