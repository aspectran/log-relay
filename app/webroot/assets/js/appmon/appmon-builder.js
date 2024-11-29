function AppMonBuilder() {

    const endpoints = [];

    this.build = function (basePath, token, currentEndpoint) {
        $.ajax({
            url: basePath + "appmon/monitoring/endpoints/" + token,
            type: 'get',
            dataType: "json",
            success: function (data) {
                if (data) {
                    endpoints.length = 0;
                    let index = 0;
                    for (let key in data) {
                        let endpoint = data[key];
                        endpoint['index'] = index++;
                        endpoint['basePath'] = basePath;
                        if (!currentEndpoint || currentEndpoint === endpoint.name) {
                            endpoints.push(endpoint);
                        }
                    }
                    if (endpoints.length) {
                        establishEndpoint(0);
                    }
                }
            }
        });
    };

    const establishEndpoint = function (endpointIndex) {
        console.log('endpointIndex', endpointIndex);
        function onEndpointJoined(endpoint, payload) {
            console.log('endpoint', endpoint);
            buildEndpoint(endpoint, payload);
        }
        function onEstablishCompleted(endpoint, payload) {
            if (endpointIndex < endpoints.length - 1) {
                establishEndpoint(endpointIndex + 1);
            } else if (endpointIndex === endpoints.length - 1) {
                buildGroups();
                for (let key in payload.messages) {
                    let msg = payload.messages[key];
                    endpoint.viewer.printMessage(msg);
                }
                if (endpoints.length) {
                    changeEndpoint(0);
                    if (location.hash) {
                        let name = location.hash.substring(1);
                        let endpointBox = $(".endpoint-box.available").eq(0);
                        endpointBox.find(".tabs-title.available").each(function () {
                            if ($(this).data("name") === name) {
                                $(this).addClass("is-active");
                            } else {
                                $(this).removeClass("is-active");
                            }
                        });
                        changeGroup(endpointBox, name);
                    }
                }
            }
        }
        function onErrorObserved(endpoint) {
            let client = new AppmonPollingClient(endpoint, onEndpointJoined, onEstablishCompleted);
            endpoint['client'] = client;
            client.start();
        }

        if (endpointIndex === 0) {
            clearScreen();
        }

        let endpoint = endpoints[endpointIndex];
        endpoint['viewer'] = new AppmonViewer();
        let client = new AppmonWebsocketClient(endpoint, onEndpointJoined, onEstablishCompleted, onErrorObserved);
        endpoint['client'] = client;
        client.start();
    };

    const clearScreen = function () {
        $(".endpoint.tabs .tabs-title.available").remove();
        $(".endpoint.tabs .tabs-title").show();
        $(".endpoint-box.available").remove();
        $(".endpoint-box").show();
    };

    const changeEndpoint = function (endpointIndex) {
        for (let key in endpoints) {
            endpoints[key].viewer.setVisible(false);
        }
        $(".endpoint-box.available").hide().eq(endpointIndex).show();
        endpoints[endpointIndex].viewer.setVisible(true);
        let logtails = endpoints[endpointIndex].viewer.getLogtails();
        for (let key in logtails) {
            let logtail = logtails[key];
            if (!logtail.data("pause")) {
                endpoints[endpointIndex].viewer.refresh(logtail);
            }
        }
    };

    const changeGroup = function (endpointBox, groupName) {
        let endpointIndex = endpointBox.data("index");
        endpointBox.find(".group-box").hide();
        let groupBox = endpointBox.find(".group-box[data-name=" + groupName + "]").show();
        groupBox.find(".logtail-box.available .logtail").each(function () {
            let logtail = $(this);
            if (!logtail.data("pause")) {
                endpoints[endpointIndex].viewer.refresh(logtail);
            }
        });
    };

    const buildEndpoint = function (endpoint, payload) {
        let endpointBox = addEndpoint(endpoint);
        for (let key in payload.groups) {
            let group = payload.groups[key];
            addGroup(endpointBox, group);
        }
        for (let key in payload.logtails) {
            let logtail = payload.logtails[key];
            let logtailBox = addLogtail(endpointBox, logtail);
            endpoint.viewer.setLogtail(logtail.name, logtailBox.find(".logtail"));
        }
        for (let key in payload.statuses) {
            let status = payload.statuses[key];
            let statusBox = addStatus(endpointBox, status);
            endpoint.viewer.setStatus(status.name, statusBox);
        }
        endpointBox.find(".logtail-box.available").each(function() {
            let logtail = $(this).find(".logtail");
            let groupName = $(this).data("group");
            let logtailName = logtail.data("logtail-name");
            let logtailBox = logtail.closest(".logtail-box");
            let missileTrack = logtailBox.find(".missile-track.available");
            if (missileTrack.length > 0) {
                endpoint.viewer.setMissileTrack(logtailName, missileTrack);
            }
            let indicator1 = $(".endpoint.tabs .tabs-title.available .indicator").eq(endpoint.index);
            let indicator2 = endpointBox.find(".group.tabs .tabs-title.available[data-name=" + groupName + "]").find(".indicator");
            let indicator3 = logtailBox.find(".status-bar");
            endpoint.viewer.setIndicators(logtailName, [indicator1, indicator2, indicator3]);
            logtail.data("tailing", true);
            logtailBox.find(".tailing-status").addClass("on");
        });
        if (endpoint.mode === "polling") {
            $("ul.speed-options").show();
        }
    };

    const buildGroups = function () {
        $(".endpoint.tabs .tabs-title.available").eq(0).addClass("is-active");
        $(".endpoint.tabs .tabs-title.available a").click(function() {
            $(".endpoint.tabs .tabs-title").removeClass("is-active");
            let tab = $(this).closest(".tabs-title");
            tab.addClass("is-active");
            let endpointIndex = tab.data("index");
            changeEndpoint(endpointIndex);
        });
        $(".endpoint-box.available .tabs .tabs-title.available").each(function() {
            let endpointIndex = $(this).data("index");
            let endpointBox = $(".endpoint-box.available").eq(endpointIndex);
            let groupTab = endpointBox.find(".group.tabs .tabs-title.available").eq(0);
            let groupName = groupTab.addClass("is-active").data("name");
            changeGroup(endpointBox, groupName);
        });
        $(".group.tabs .tabs-title.available a").click(function() {
            let groupTab = $(this).closest(".tabs-title");
            let groupName = groupTab.data("name");
            $(".endpoint-box.available").each(function () {
                let endpointBox2 = $(this);
                $(endpointBox2).find(".group.tabs .tabs-title.available").each(function () {
                    let groupTab = $(this);
                    if (groupTab.data("name") === groupName) {
                        if (!groupTab.hasClass("is-active")) {
                            groupTab.addClass("is-active");
                            changeGroup(endpointBox2, groupName);
                        }
                    } else {
                        groupTab.removeClass("is-active");
                    }
                });
            });
        });
        $(".logtail-box .tailing-switch").click(function() {
            let logtail = $(this).closest(".logtail-box").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            if (logtail.data("tailing")) {
                logtail.data("tailing", false);
                $(this).find(".tailing-status").removeClass("on");
            } else {
                logtail.data("tailing", true);
                $(this).find(".tailing-status").addClass("on");
                endpoint.viewer.refresh(logtail);
            }
        });
        $(".logtail-box .pause-switch").click(function() {
            let logtail = $(this).closest(".logtail-box").find(".logtail");
            if (logtail.data("pause")) {
                logtail.data("pause", false);
                $(this).removeClass("on");
            } else {
                logtail.data("pause", true);
                $(this).addClass("on");
            }
        });
        $(".logtail-box .clear-screen").click(function() {
            let logtail = $(this).closest(".logtail-box").find(".logtail");
            let endpointIndex = logtail.data("endpoint-index");
            let endpoint = endpoints[endpointIndex];
            endpoint.viewer.clear(logtail);
        });
        $(".layout-options li a").click(function() {
            let endpointBox = $(this).closest(".endpoint-box");
            let logtailBox = endpointBox.find(".logtail-box.available");
            let liStacked = $(".layout-options li.stacked");
            let liTabbed = $(".layout-options li.tabbed");
            let li = $(this).parent();
            if (!li.hasClass("on")) {
                if (li.hasClass("tabbed")) {
                    liTabbed.addClass("on");
                    liStacked.removeClass("on");
                    endpointBox.removeClass("stacked");
                } else if (li.hasClass("stacked")) {
                    liTabbed.removeClass("on");
                    liStacked.addClass("on");
                    endpointBox.addClass("stacked");
                } else if (li.hasClass("compact")) {
                    li.addClass("on vertical");
                    endpointBox.addClass("compact vertical");
                }
            } else {
                if (li.hasClass("compact")) {
                    if (li.hasClass("vertical")) {
                        li.removeClass("vertical").addClass("horizontal");
                        endpointBox.removeClass("vertical").addClass("horizontal");
                        logtailBox.addClass("large-6");
                    } else if (li.hasClass("horizontal")) {
                        li.removeClass("on horizontal");
                        endpointBox.removeClass("compact horizontal");
                        logtailBox.removeClass("large-6");
                    }
                }
            }
            let endpointIndex = endpointBox.data("index");
            endpointBox.find(".logtail-box.available").each(function () {
                if ($(this).find(".tailing-status").hasClass("on")) {
                    endpoints[endpointIndex].viewer.refresh();
                }
            });
        });
        $(".speed-options li").click(function() {
            let endpointBox = $(this).closest(".endpoint-box");
            let endpointIndex = endpointBox.data("index");
            let liFast = $(".speed-options li.fast");
            if (liFast.hasClass("on")) {
                liFast.removeClass("on");
                endpoints[endpointIndex].client.speed(0);
            } else {
                liFast.addClass("on");
                endpoints[endpointIndex].client.speed(1);
            }
        });
    };

    const addEndpoint = function (endpoint) {
        let tabs = $(".endpoint.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let tab = tab0.hide().clone();
        tab.addClass("available");
        tab.attr("data-index", endpoint.index);
        tab.attr("data-name", endpoint.name);
        tab.attr("data-title", endpoint.title);
        tab.attr("data-endpoint", endpoint.url);
        let a = tab.find("a");
        a.find(".title").text(" " + endpoint.title + " ");
        tab.show().appendTo(tabs);
        let endpointBox = $(".endpoint-box");
        return endpointBox.eq(0).hide().clone()
            .addClass("available")
            .attr("data-index", endpoint.index)
            .attr("data-name", endpoint.name)
            .attr("data-title", endpoint.title)
            .insertAfter(endpointBox.last()).show();
    };

    const addGroup = function (endpointBox, group) {
        let endpointTitle = endpointBox.data("title");
        let tabs = endpointBox.find(".group.tabs");
        let tab0 = tabs.find(".tabs-title").eq(0);
        let index = tabs.find(".tabs-title").length - 1;
        let tab = tab0.hide().clone()
            .addClass("available")
            .attr("data-index", index)
            .attr("data-name", group.name)
            .attr("title", endpointTitle + " ›› " + group.title);
        tab.find("a .title").text(" " + group.title + " ");
        tab.show().appendTo(tabs);
        let groupBox = endpointBox.find(".group-box").eq(0).hide().clone();
        groupBox.addClass("available")
            .attr("data-name", group.name)
            .attr("data-title", group.title);
        return groupBox.appendTo(endpointBox);
    };

    const addLogtail = function (endpointBox, logtail) {
        let endpointIndex = endpointBox.data("index");
        let endpointTitle = endpointBox.data("title");
        let groupBox = endpointBox.find(".group-box[data-name=" + logtail.group + "]");
        let logtailBox = groupBox.find(".logtail-box").eq(0).hide().clone();
        logtailBox.addClass("large-6 available");
        logtailBox.attr("data-group", logtail.group);
        logtailBox.attr("data-name", logtail.name);
        logtailBox.find(".status-bar h4").text(endpointTitle + " ›› " + logtail.file);
        logtailBox.find(".logtail")
            .attr("data-endpoint-index", endpointIndex).attr("data-endpoint-name", endpointTitle)
            .attr("data-logtail-name", logtail.name);
        if (logtail.visualizing) {
            logtailBox.addClass("with-track");
            logtailBox.find(".missile-track")
                .addClass("available")
                .attr("data-visualizing", logtail.visualizing)
                .show();
        } else {
            logtailBox.addClass("no-track");
        }
        return logtailBox.appendTo(groupBox.find(".logtail-box-wrap")).show();
    };

    const addStatus = function (endpointBox, status) {
        let groupBox = endpointBox.find(".group-box[data-name=" + status.group + "]");
        let statusBox = groupBox.find(".status-box").eq(0).hide().clone();
        statusBox.addClass("available")
            .attr("data-group", status.group)
            .attr("data-name", status.name);
        return statusBox.appendTo(groupBox).show();
    };
}
