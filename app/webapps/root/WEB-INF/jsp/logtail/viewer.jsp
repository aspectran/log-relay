<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="endpoint-content">
    <dl class="logtails tabs b0">
        <dd class="tabs-title"><a><span class="bullet fi-list-bullet"></span> <span class="title"> </span> <span class="indicator fi-loop"></span></a></dd>
    </dl>
    <div class="logtail-content">
        <div class="status">
            <h4 class="ellipses"></h4>
            <a href="#" class="tailing-switch" title="Scroll to End of Log">
                <span class="tailing-status"></span>
            </a>
        </div>
        <div class="missile-track" style="display: none">
            <div class="stack"></div>
        </div>
        <pre class="logtail"></pre>
    </div>
</div>
<link rel="stylesheet" href="/assets/css/logtail-viewer.css">
<script src="/assets/js/logtail-viewer.js"></script>
<script>
    const endpoints = [];

    $(function() {
        $.ajax({
            url: "/endpoints/${page.token}",
            type: 'get',
            dataType: "json",
            success: function(data) {
                if (data) {
                    for (let key in data) {
                        endpoints.push(data[key]);
                    }
                    for (let index = 0; index < endpoints.length; index++) {
                        establishEndpoint(index);
                    }
                }
            }
        });
    });

    function establishEndpoint(endpointIndex) {
        function endpointEstablished(endpoint, tailers) {
            let endpointContent = drawLogtailsTabs(endpoint, tailers);
            endpointContent.find(".logtail-content.available").each(function() {
                let logtail = $(this).find(".logtail");
                let logtailIndex = logtail.data("logtail-index");
                let logtailName = logtail.data("logtail-name");
                let logtailContent = logtail.closest(".logtail-content");

                endpoint.viewer.logtails[logtailName] = logtail;

                let missileTrack = logtailContent.find(".missile-track.available");
                endpoint.viewer.missileTracks[logtailName] = (missileTrack.length > 0 ? missileTrack : null);

                let indicator1 = $(".endpoints.tabs .tabs-title.available .indicator").eq(endpointIndex);
                let indicator2 = $(".logtails.tabs .tabs-title.available .indicator").eq(logtailIndex);
                endpoint.viewer.indicators[logtailName] = [indicator1, indicator2];

                logtail.data("tailing", true);
                logtailContent.find(".tailing-status").addClass("active");
            });
        }
        function establishCompleted() {
            if (endpointIndex < endpoints.length - 1) {
                establishEndpoint(++endpointIndex);
            } else {
                initializeTabs();
            }
        }
        let logViewer = new LogtailViewer(endpoints[endpointIndex], endpointEstablished, establishCompleted);
        try {
            logViewer.openSocket();
        } catch (e) {
            logViewer.printErrorMessage("Socket connection failed");
        }
    }

    function initializeTabs() {
        $(".endpoints.tabs .tabs-title.available").removeClass("is-active").eq(0).addClass("is-active");
        $(".endpoint-content.available").hide().eq(0).show();
        $(".endpoints.tabs .tabs-title.available").each(function() {
            let index = $(this).data("index");
            let endpointContent = $(".endpoint-content.available").eq(index);
            endpointContent.find(".logtails.tabs .tabs-title.available").removeClass("is-active").eq(0).addClass("is-active");
            endpointContent.find(".logtail-content.available").hide().eq(0).show();
        });
        $(".endpoints.tabs .tabs-title.available a").click(function() {
            $(".endpoints.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
            let index = tab.data("index");
            tab.addClass("is-active");
            $(".endpoint-content.available").hide().eq(index).show();
            endpoints[index].viewer.refresh();
        });
        $(".logtails.tabs .tabs-title.available a").click(function() {
            let endpointContent = $(this).closest(".endpoint-content");
            let endpointIndex = endpointContent.data("index");
            let logtailTab = $(this).closest(".tabs-title");
            let logtailIndex = logtailTab.data("index");
            endpointContent.find(".logtails.tabs .tabs-title").removeClass("is-active");
            logtailTab.addClass("is-active");
            let logtailContent = endpointContent.find(".logtail-content.available").hide().eq(logtailIndex).show();
            let logtail = logtailContent.find(".logtail");
            endpoints[endpointIndex].viewer[endpointIndex].refresh(logtail);
        });
        $(".logtail-content .tailing-switch").click(function () {
            let logtail = $(this).closest(".logtail-content").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            if (logtail.data("tailing")) {
                logtail.data("tailing", false);
                $(this).find(".tailing-status").removeClass("active");
            } else {
                logtail.data("tailing", true);
                $(this).find(".tailing-status").addClass("active");
                endpoint.viewer.scrollToBottom(logtail);
            }
        });
    }

    function drawLogtailsTabs(endpoint, tailers) {
        let endpointContent = addEndpointsTab(endpoint);
        for (let key in tailers) {
            let tailer = tailers[key];
            addLogtailsTab(endpointContent, tailer);
        }
        return endpointContent;
    }

    function addEndpointsTab(endpoint) {
        let tabs = $(".endpoints.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone();
        tab.addClass("available");
        tab.data("index", index);
        tab.data("endpoint", endpoint.url);
        let a = tab.find("a");
        a.find(".title").text(" " + endpoint.name + " ");
        tab.show().appendTo(tabs);
        let content = $(".endpoint-content").eq(0).hide().clone();
        content.addClass("available");
        content.data("index", index).data("name", endpoint.name);
        content.insertAfter($(".endpoint-content").last());
        return content;
    }

    function addLogtailsTab(endpointContent, tailer) {
        let endpointIndex = endpointContent.data("index");
        let endpointName = endpointContent.data("name");
        let tabs = endpointContent.find(".logtails.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone();
        tab.addClass("available");
        tab.data("index", index);
        tab.attr("title", endpointName + " :: " + tailer.name);
        let a = tab.find("a");
        a.find(".title").text(" " + tailer.name + " ");
        tab.show().appendTo(tabs);
        let content = endpointContent.find(".logtail-content").eq(0).hide().clone();
        content.addClass("available");
        content.data("index", index).data("name", tailer.name);
        content.find(".status h4").text("( " + endpointName + " )  " + tailer.file);
        content.find(".logtail")
            .data("endpoint-index", endpointIndex).data("endpoint-name", endpointName)
            .data("logtail-index", index).data("logtail-name", tailer.name);
        content.insertAfter($(".logtail-content").last());
        if (tailer.visualizer) {
            content.find(".missile-track")
                .addClass("available")
                .data("launcher", tailer.visualizer)
                .show();
        }
        return content.show();
    }
</script>