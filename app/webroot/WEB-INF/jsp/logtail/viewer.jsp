<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="/assets/css/logtail-viewer.css">
<script src="/assets/js/logtail-viewer.js"></script>
<div class="grid-x endpoint-content">
    <div class="cell options">
        <ul class="layout-options">
            <li class="fi-layout tabbed on"><a> Tabbed layout</a></li>
            <li class="fi-layout tiled hide-for-small-only" data-columns="2"><a> 2-column layout</a></li>
            <li class="fi-layout tiled hide-for-small-only" data-columns="3"><a> 3-column layout</a></li>
            <li class="fi-layout stacked show-for-small-only" data-columns="1"><a> Stacked layout</a></li>
        </ul>
    </div>
    <dl class="cell logtails tabs b0">
        <dd class="tabs-title"><a><span class="bullet fi-list-bullet"></span> <span class="title"> </span> <span class="indicator fi-loop"></span></a></dd>
    </dl>
    <div class="cell logtail-content">
        <div class="status-bar">
            <h4 class="ellipses"></h4>
            <a href="#" class="tailing-switch" title="Scroll to End of Log">
                <span class="tailing-status"></span>
            </a>
            <a href="#" class="clear-screen" title="Clear screen">
                <span class="icon fi-x"></span>
            </a>
            <a href="#" class="pause-switch" title="Pause log output">
                <span class="icon fi-pause"></span>
            </a>
        </div>
        <div class="missile-track" style="display: none">
            <div class="stack"></div>
        </div>
        <pre class="logtail"></pre>
    </div>
</div>
<script>
    const endpoints = [];

    $(function() {
        const endpoint = "${page.endpoint}";
        $.ajax({
            url: "/endpoints/${page.token}",
            type: 'get',
            dataType: "json",
            success: function(data) {
                if (data) {
                    for (let key in data) {
                        if (!endpoint || endpoint === data[key].name) {
                            endpoints.push(data[key]);
                        }
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
                let indicator3 = logtailContent.find(".status-bar");
                endpoint.viewer.indicators[logtailName] = [indicator1, indicator2, indicator3];

                logtail.data("tailing", true);
                logtailContent.find(".tailing-status").addClass("on");
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
            let endpointIndex = $(this).data("index");
            let endpointContent = $(".endpoint-content.available").eq(endpointIndex);
            endpointContent.find(".logtails.tabs .tabs-title.available").removeClass("is-active").eq(0).addClass("is-active");
            endpointContent.find(".logtail-content.available").hide().eq(0).show();
        });
        $(".endpoints.tabs .tabs-title.available a").click(function() {
            $(".endpoints.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
            let endpointIndex = tab.data("index");
            tab.addClass("is-active");
            $(".endpoint-content.available").hide().eq(endpointIndex).show();
            let logtails = endpoints[endpointIndex].viewer.logtails;
            for (let key in logtails) {
                let logtail = logtails[key];
                if (!logtail.data("pause")) {
                    endpoints[endpointIndex].viewer.refresh(logtail);
                }
            }
        });
        $(".logtails.tabs .tabs-title.available a").click(function() {
            let endpointContent = $(this).closest(".endpoint-content");
            let endpointIndex = endpointContent.data("index");
            let logtailTab = $(this).closest(".tabs-title");
            let logtailIndex = logtailTab.data("index");
            if (!logtailTab.hasClass("is-active")) {
                endpointContent.find(".logtails.tabs .tabs-title").removeClass("is-active");
                logtailTab.addClass("is-active");
                let logtailContent = endpointContent.find(".logtail-content.available").hide().eq(logtailIndex).show();
                let logtail = logtailContent.find(".logtail");
                if (!logtail.data("pause")) {
                    endpoints[endpointIndex].viewer.refresh(logtail);
                }
            }
        });
        $(".logtails.tabs .tabs-title.available a").dblclick(function(event) {
            let endpointContent = $(this).closest(".endpoint-content");
            endpointContent.find(".logtails.tabs .tabs-title").removeClass("is-active");
            $(this).click();
            event.preventDefault();
        });
        $(".logtail-content .tailing-switch").click(function() {
            let logtail = $(this).closest(".logtail-content").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            if (logtail.data("tailing")) {
                logtail.data("tailing", false);
                $(this).find(".tailing-status").removeClass("on");
            } else {
                logtail.data("tailing", true);
                $(this).find(".tailing-status").addClass("on");
                endpoint.viewer.scrollToBottom(logtail);
            }
        });
        $(".logtail-content .pause-switch").click(function() {
            let logtail = $(this).closest(".logtail-content").find(".logtail");
            if (logtail.data("pause")) {
                logtail.data("pause", false);
                $(this).removeClass("on");
            } else {
                logtail.data("pause", true);
                $(this).addClass("on");
            }
        });
        $(".logtail-content .clear-screen").click(function() {
            let logtail = $(this).closest(".logtail-content").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            endpoint.viewer.clear(logtail);
        });

        $(".layout-options li a").click(function() {
            $(".layout-options li").removeClass("on");
            $(this).parent().addClass("on");
            let endpointContent = $(this).closest(".endpoint-content");
            let logtailContent = endpointContent.find(".logtail-content");
            let columns = $(this).parent().data("columns");
            switch (columns) {
                case 1:
                    endpointContent.addClass("tiled");
                    logtailContent.removeClass("large-3 large-4 large-6");
                    break;
                case 2:
                    endpointContent.addClass("tiled");
                    logtailContent.removeClass("large-3 large-4 large-6").addClass("large-6");
                    break;
                case 3:
                    endpointContent.addClass("tiled");
                    logtailContent.removeClass("large-3 large-4 large-6").addClass("large-4");
                    break;
                default:
                    endpointContent.removeClass("tiled");
                    logtailContent.removeClass("large-3 large-4 large-6");
                    break;
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
        tab.data("name", endpoint.name);
        tab.data("title", endpoint.title);
        tab.data("endpoint", endpoint.url);
        let a = tab.find("a");
        a.find(".title").text(" " + endpoint.title + " ");
        tab.show().appendTo(tabs);
        let content = $(".endpoint-content").eq(0).hide().clone();
        content.addClass("available");
        content.data("index", index).data("name", endpoint.name).data("title", endpoint.title);
        content.insertAfter($(".endpoint-content").last());
        return content;
    }

    function addLogtailsTab(endpointContent, tailer) {
        let endpointIndex = endpointContent.data("index");
        let endpointTitle = endpointContent.data("title");
        let tabs = endpointContent.find(".logtails.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone();
        tab.addClass("available");
        tab.data("index", index);
        tab.attr("title", endpointTitle + " ›› " + tailer.name);
        let a = tab.find("a");
        a.find(".title").text(" " + tailer.name + " ");
        tab.show().appendTo(tabs);
        let content = endpointContent.find(".logtail-content").eq(0).hide().clone();
        content.addClass("available");
        content.data("index", index).data("name", tailer.name);
        content.find(".status-bar h4").text(endpointTitle + " ›› " + tailer.file);
        content.find(".logtail")
            .data("endpoint-index", endpointIndex).data("endpoint-name", endpointTitle)
            .data("logtail-index", index).data("logtail-name", tailer.name);
        content.insertAfter($(".logtail-content").last());
        if (tailer.visualizer) {
            content.addClass("with-track");
            content.find(".missile-track")
                .addClass("available")
                .data("launcher", tailer.visualizer)
                .show();
        }
        return content.show();
    }
</script>