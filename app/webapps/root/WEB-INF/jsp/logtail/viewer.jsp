<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="endpoint-content">
    <dl class="logtails tabs b0" data-tabs>
        <dd class="tabs-title"><a href="#1"><span class="bullet icon-archive"></span> <span class="title">Logtail</span> <span class="indicator icon-eye"></span></a></dd>
    </dl>
    <div class="logtail-content">
        <div class="status">
            <h4 class="ellipses"></h4>
            <a class="bite-tail" title="Scroll to End of Log">
                <span class="tail-status"></span>
            </a>
        </div>
        <div class="missile-route">
            <div class="stack"></div>
        </div>
        <pre class="logtail"></pre>
    </div>
</div>
<link rel="stylesheet" href="/assets/css/logtail-viewer.css">
<script src="/assets/js/logtail-viewer.js"></script>
<script>
    const endpoints = [
        {
            name: "Server-1",
            url: "/logtail"
        },
        {
            name: "Server-2",
            url: "/logtail"
        }
    ];

    $(function() {
        for (let key in endpoints) {
            let endpoint = endpoints[key];
            let logViewer = new LogtailViewer(endpoint, drawLogtailsTabs);
            try {
                logViewer.openSocket();
            } catch (e) {
                logViewer.printErrorMessage("Socket connection failed");
            }
        }
        $("dl.endpoints .tabs-title, dl.logtails .tabs-title").removeClass("is-active");
        $("dl.endpoints .tabs-title:eq(0), dl.logtails .tabs-title:eq(0)").addClass("is-active");
        $("dl.endpoints, dl.logtails").addClass("tabs");
    });

    function drawLogtailsTabs(viewer, endpoint, tailers) {
        let endpointContent = addEndpointsTab(endpoint);
        for (let key in tailers) {
            let tailer = tailers[key];
            let logtailContent = addLogtailsTab(endpointContent, tailer);
            logtailContent.find(".bite-tail").click(function() {
                let logtail = logtailContent.find(".logtail");
                viewer.switchTailBite(logtail, !logtail.data("bite"));
            });
        }
        return endpointContent;
    }

    function addEndpointsTab(endpoint) {
        let tabs = $("dl.endpoints");
        let tab0 = tabs.find(".tabs-title:eq(0)");
        let index = tabs.find(".tabs-title").length;
        let tabId = "endpoint-tab-" + index;
        let contentId = "endpoint-content-" + index;
        let tab = tab0.hide().clone();
        tab.attr("id", tabId);
        let a = tab.find("a");
        a.attr("href", "#" + contentId);
        a.find(".title").text(" " + endpoint.name + " ");
        tab.show().appendTo(tabs);
        let content = $(".endpoint-content:eq(0)").hide().clone();
        content.attr("id", contentId);
        content.data("tab-id", tabId);
        content.insertAfter($(".endpoint-content").last());
        return content.show();
    }

    function addLogtailsTab(endpointContent, tailer) {
        let tabs = endpointContent.find("dl.logtails");
        let tab0 = tabs.find(".tabs-title:eq(0)");
        let index = tabs.find(".tabs-title").length;
        let tabId = "logtail-tab-" + index;
        let contentId = "logtail-content-" + index;
        let tab = tab0.hide().clone();
        tab.attr("id", tabId);
        let a = tab.find("a");
        a.attr("href", "#" + contentId);
        a.find(".title").text(" " + tailer.name + " ");
        tab.show().appendTo(tabs);
        let content = $(".logtail-content:eq(0)").hide().clone();
        content.attr("id", contentId);
        content.find(".status h4").text(tailer.file);
        let logtail = content.find(".logtail");
        logtail.attr("id", "logtail-" + tailer.name);
        return content.show();
    }
</script>