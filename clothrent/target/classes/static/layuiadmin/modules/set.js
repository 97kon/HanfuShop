/** layuiAdmin.std-v1.0.0-beta9 LPPL License By http://www.layui.com/admin/ */
;layui.define(["form", "upload"], function () {
    var
        e = layui.layer,
        n = layui.form;

        n.on("submit(setmyinfo)", function (t) {
        return e.msg(JSON.stringify(t.field))
    })
});