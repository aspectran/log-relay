<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://aspectran.com/tags" prefix="aspectran" %>
<link rel="stylesheet" href="<aspectran:url value="/assets/css/appmon/appmon.css?20241118"/>">
<script src="<aspectran:url value="/assets/js/appmon/appmon-builder.js?20241129"/>"></script>
<script src="<aspectran:url value="/assets/js/appmon/appmon-viewer.js?20241118"/>"></script>
<script src="<aspectran:url value="/assets/js/appmon/appmon-websocket-client.js?20241118"/>"></script>
<script src="<aspectran:url value="/assets/js/appmon/appmon-polling-client.js?20241118"/>"></script>
<div class="grid-x endpoint-box compact horizontal">
    <div class="cell options t10 b5">
        <ul class="speed-options">
            <li class="fi-fast-forward fast" title="Set to poll every second. Turn this option on only when absolutely necessary."></li>
        </ul>
        <ul class="layout-options">
            <li class="fi-layout tabbed on" data-columns="0"><a> Tabbed</a></li>
            <li class="fi-layout stacked" data-columns="0"><a> Stacked</a></li>
            <li class="fi-layout compact on horizontal hide-for-small-only"><a> Compact</a></li>
        </ul>
    </div>
    <dl class="cell group tabs b0">
        <dd class="tabs-title"><a><span class="bullet fi-list-bullet"></span>
            <span class="title"> </span> <span class="indicator fi-loop"></span></a>
        </dd>
    </dl>
    <div class="cell group-box">
        <div class="grid-x logtail-box-wrap">
            <div class="cell logtail-box">
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
            <div class="cell status-box">
                <div class="grid-x">
                    <div class="cell small-12 large-4">
                        <div class="panel">
                            <dl class="sessions">
                                <dt>Current Active Sessions</dt>
                                <dd><span class="number activeSessionCount">0</span></dd>
                                <dt>Current Inactive Sessions</dt>
                                <dd><span class="number evictedSessionCount">0</span></dd>
                                <dt>Highest Active Sessions</dt>
                                <dd><span class="number highestActiveSessionCount">0</span></dd>
                                <dt title="Number of sessions created since system bootup">Created Sessions</dt>
                                <dd><span class="number createdSessionCount">0</span></dd>
                                <dt>Expired Sessions</dt>
                                <dd><span class="number expiredSessionCount">0</span></dd>
                                <dt>Rejected Sessions</dt>
                                <dd><span class="number rejectedSessionCount">0</span></dd>
                            </dl>
                            <p class="text-right"><i>Elapsed <span class="elapsed"></span></i></p>
                        </div>
                    </div>
                    <div class="cell small-12 large-8">
                        <div class="panel sessions-box">
                            <ul class="sessions">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $(function () {
        const token = "${page.token}";
        const endpoint = "${page.endpoint}";
        new AppMonBuilder().build("<aspectran:url value="/"/>", token, endpoint);
    });
</script>
