/**
 * 获取光标位置
 * @param cursor 光标
 * @returns {number} 光标所在位置
 */
function getCursorPosition(cursor) {
    const ch = cursor.ch;
    const line = cursor.line;
    const data = window.editor.getValue();
    const messages = data.split("\n");
    let length = 0;
    $.each(messages, function (index, value) {
        if (index === line) {
            length = length + ch;
            return false;
        }
        length = length + value.length + 1;
    });
    return length;
}

/**
 * 替换选中内容
 * @param text 文本
 * @param start 开始位置
 * @param stop 结尾位置
 * @param replaceText 替换文本
 * @returns {string} 替换结果文本
 */
function replacePos(text, start, stop, replaceText) {
    if (start > stop) {
        const tmp = stop;
        stop = start;
        start = tmp;
    }
    return text.substring(0, start) + replaceText + text.substring(stop);
}

/**
 * 获取sql信息
 * @param selected
 */
function getAppInfo(selected) {
    if (selected !== "") {
        const data = {
            "appName": selected
        };
        $.ajax({
            url: "./getAppInfo",
            type: "POST",
            dataType: "json",
            data: JSON.stringify(data),
            contentType: "application/json",
            async: false,
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(errorThrown);
            },
            success: function (data) {
                const envs = data['envs'];
                const sqlInfos = data['sqlInfos'];
                const envSelect = $("#env")[0];
                const sqlSelect = $("#sql")[0];
                envSelect.innerHTML = "";
                sqlSelect.innerHTML = "<option selected='selected' value='' sql=''>临时脚本,刷新清空</option>";
                $.each(envs, function (index, value) {
                    envSelect.innerHTML += "<option value=" + index + ">" + value + "</option>"
                });
                $.each(sqlInfos, function (index, value) {
                    sqlSelect.innerHTML += "<option value='" + value.id + "'>" + value.sql.split("\n")[0].replace(/--/, "") + "</option>"
                });
            }
        });
    }
}

/**
 * 保存sql
 */
function saveSQL() {
    const appName = $("#select option:selected").val();
    const sqlId = $("#sql option:selected").val()
    const sql = window.editor.getValue()
    if (appName !== "" && !(sqlId === "" && sql === "")) {
        const data = {
            "appName": appName,
            "sqlId": sqlId,
            "sql": sql
        }
        $.ajax({
            url: "./saveSqlInfo",
            type: "POST",
            dataType: "json",
            data: JSON.stringify(data),
            async: false,
            contentType: "application/json",
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(errorThrown);
            },
            success: function (data) {
                const id = data['selected']
                const sqlInfos = data['sqlInfos'];
                const sqlSelect = $("#sql")[0];
                if (id === null) {
                    sqlSelect.innerHTML = "<option selected='selected' value='' sql=''>临时脚本,刷新清空</option>";
                } else {
                    sqlSelect.innerHTML = "<option value='' sql=''>临时脚本,刷新清空</option>";
                }
                $.each(sqlInfos, function (index, value) {
                    if (id === value.id) {
                        sqlSelect.innerHTML += "<option selected='selected' value='" + value.id + "'>" + value.sql.split("\n")[0].replace(/--/, "") + "</option>"
                    } else {
                        sqlSelect.innerHTML += "<option value='" + value.id + "'>" + value.sql.split("\n")[0].replace(/--/, "") + "</option>"
                    }
                });
            }
        });
    }
    $("#boot").html("新增脚本");
}

/**
 * 获取sql
 */
function getSqlById(sqlId) {
    let sql = "";
    const data = {
        "sqlId": sqlId,
    }
    $.ajax({
        url: "./getSqlById",
        type: "POST",
        dataType: "json",
        data: JSON.stringify(data),
        async: false,
        contentType: "application/json",
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(errorThrown);
        },
        success: function (data) {
            sql = data['sql'];
        }
    });
    return sql;
}

/**
 * 查询sql
 */
function executeSQL(length) {
    const appName = $("#select option:selected").val();
    const env = $("#env option:selected").html();
    const sql = window.editor.getValue();
    if (appName !== "" && env !== "" && sql !== "") {
        const data = {
            "appName": appName,
            "env": env,
            "sql": sql,
            "cursor": length
        }
        $.ajax({
            url: "./executeSQL",
            type: "POST",
            dataType: "json",
            data: JSON.stringify(data),
            async: false,
            contentType: "application/json",
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(errorThrown);
            },
            success: function (data) {
                debugger;
                const results = data["results"];
                const resultTab = $("#resultTab");
                const resultTabContent = $("#resultTabContent");
                resultTab.empty();
                resultTabContent.empty();
                $.each(results, function (index, value) {
                    let resultTabStr = "<li class='nav-item' role='presentation'><button class='nav-link";
                    let resultTabContentStr = "<div class='tab-pane fade show";
                    if (index === 0) {
                        resultTabStr += " active";
                        resultTabContentStr += " active";
                    }
                    resultTabStr += "' id='tab_" + index + "' data-bs-toggle='tab' data-bs-target='#tabContent_" + index + "' type='button' role='tab' aria-controls='tabContent_" + index + "' aria-selected='true'>" + value["sql"] + "</button></li>";
                    resultTabContentStr += "' id='tabContent_" + index + "' role='tabpanel' aria-labelledby='tab_" + index + "'>" + createTable(value["head"], value["rows"]) + "</div>";
                    resultTab.append(resultTabStr);
                    resultTabContent.append(resultTabContentStr);
                });
            }
        });
    }
}

function createTable(head, rows) {
    let html = "<div class='table-responsive'><table class='table table-striped table-hover table-bordered rowSameHeight'><thead><tr>";
    $.each(head, function (index, value) {
        html += "<th>" + value + "</th>"
    });
    html += "</tr></thead><tbody>";
    $.each(rows, function (index, value) {
        html += "<tr>"
        $.each(value, function (index, value) {
            html += "<td>" + value + "</td>"
        });
        html += "</tr>"
    });
    html += "</tbody></table></div>";
    return html;
}

function getTableAndColumn(model) {
    const appName = $("#select option:selected").val();
    const env = $("#env option:selected").html();
    if (appName !== "" && env !== "") {
        const data = {
            "appName": appName,
            "env": env,
            "model": model
        };
        $.ajax({
            url: "./getTableAndColumn",
            type: "POST",
            dataType: "json",
            data: JSON.stringify(data),
            contentType: "application/json",
            async: false,
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(errorThrown);
            },
            success: function (data) {
                const tableAndColumn = JSON.parse(data['tableAndColumn']);
                const option = window.editor.getOption("hintOptions");
                CodeMirror.heightKeys = {};
                option.tables = {};
                $.each(tableAndColumn, function (index, value) {
                    //设置提示
                    option.tables[index] = value;
                });
            }
        });
    }
}

window.onload = function () {
    const mime = "text/x-mysql";
    window.editor = CodeMirror.fromTextArea(
        document.getElementById("code"),
        {
            mode: mime,
            indentWithTabs: true,
            smartIndent: true,
            lineNumbers: true,
            lineWrapping: true,
            theme: "neat",
            matchBrackets: true,
            autofocus: true,
            spellcheck: true,
            extraKeys: {
                "Ctrl-Enter": function () {
                    const length = getCursorPosition(window.editor.getCursor());
                    executeSQL(length);
                },
                "Ctrl-S": function () {
                    const all = window.editor.getValue();
                    const data = window.editor.getSelection().trim();
                    if (data === "") {
                        return;
                    }
                    const messages = data.split("\n");
                    let pack = "";
                    $.each(messages, function (index, value) {
                        value = value.trim();
                        if (index === 0) {
                            pack = "('" + value + "', ";
                        } else {
                            pack = pack + "'" + value + "', ";
                        }
                    });
                    pack = pack.substring(0, pack.length - 2);
                    pack = pack + ")";

                    const start = window.editor.doc.sel.ranges[0].anchor;
                    const stop = window.editor.doc.sel.ranges[0].head;
                    window.editor.setValue(
                        replacePos(all, getCursorPosition(start), getCursorPosition(stop), pack)
                    );
                    window.editor.setCursor(stop.line, stop.ch);
                },
                "Ctrl-A": function () {
                    const all = window.editor.getValue();
                    const data = window.editor.getSelection();
                    const messages = all.split("\n");
                    if (data === "") {
                        const line = messages.length - 1;
                        const ch = messages[messages.length - 1].length;
                        window.editor.setSelection(
                            {line: 0, ch: 0},
                            {line: line, ch: ch}
                        );
                        return;
                    }
                    let i = 0;
                    const ranges = [];
                    $.each(messages, function (row, value) {
                        if (value.indexOf(data) !== -1) {
                            let now = 0;
                            const tags = value.split(data);
                            $.each(tags, function (num, tag) {
                                const pos = value.indexOf(data, now);
                                if (pos !== -1) {
                                    ranges[i] = {
                                        anchor: {line: row, ch: pos},
                                        head: {line: row, ch: pos + data.length},
                                    };
                                    now = now + data.length + tag.length;
                                    i++;
                                }
                            });
                        }
                    });
                    window.editor.setSelections(ranges);
                },
                "Ctrl-F": function () {
                    const all = window.editor.getValue();
                    window.editor.setValue(sqlFormatter.format(all));
                },
                Tab: "autocomplete"
            },
            hint: CodeMirror.hint.sql,
            hintOptions: {tables: {}}
        }
    );
    const selected = $("#select").change().children("option:selected").val();
    getAppInfo(selected);
};
$(function () {
    $(".select2-search").select2();
    $(".select2-env").select2({
        minimumResultsForSearch: -1,
    });
    $(".select2-sql").select2({
        minimumResultsForSearch: -1,
    });
    $("#select").change(function () {
        const selected = $(this).children("option:selected").val();
        window.editor.setValue("");
        getAppInfo(selected);
        getTableAndColumn("query");
        $("#boot").html("新增脚本");
    });

    $("#env").change(function () {
        getTableAndColumn("query");
    });

    $("#sql").change(function () {
        const select = $("#sql option:selected")
        const sql = window.editor.getValue();
        const first = $(this).children("option:nth-child(1)");
        if (select.val() !== "") {
            if (first.attr("sql") === "") {
                first.attr("sql", sql);
            }
            window.editor.setValue(getSqlById(select.val()));
            $("#boot").html("修改脚本");
        } else {
            window.editor.setValue(select.attr("sql"));
            select.attr("sql", "");
            $("#boot").html("新增脚本");
        }
    });
});