function LogtailViewer(endpoint, endpointEstablished, establishCompleted) {
    this.endpoint = endpoint;
    this.endpointEstablished = endpointEstablished;
    this.establishCompleted = establishCompleted;
    this.socket = null;
    this.heartbeatTimer = null;
    this.established = false;
    this.logtails = null;
    this.pendingMessages = [];

    this.openSocket = function() {
        if (this.socket) {
            this.socket.close();
        }
        let url = new URL(this.endpoint.url, location.href);
        url.protocol = url.protocol.replace('https:', 'wss:');
        url.protocol = url.protocol.replace('http:', 'ws:');
        this.socket = new WebSocket(url.href);
        let self = this;
        this.socket.onopen = function (event) {
            self.pendingMessages.push("Socket connection successful");
            self.socket.send("JOIN:");
            self.heartbeatPing();
        };
        this.socket.onmessage = function (event) {
            if (typeof event.data === "string") {
                if (event.data === "--heartbeat-pong--") {
                    self.heartbeatPing();
                    return;
                }
                let msg = event.data;
                let idx = msg.indexOf(":");
                if (idx !== -1) {
                    if (self.established) {
                        let tailerName = msg.substring(0, idx);
                        self.printMessage(tailerName, msg.substring(idx + 1));
                    } else {
                        let command = msg.substring(0, idx);
                        if (command === "availableTailers") {
                            let payload = JSON.parse(msg.substring(idx + 1));
                            self.establish(payload);
                        }
                    }
                }
            }
        };
        this.socket.onclose = function (event) {
            self.printEventMessage('Socket connection closed. Please refresh this page to try again!');
            self.closeSocket();
        };
        this.socket.onerror = function (event) {
            console.error("WebSocket error observed:", event);
            self.printErrorMessage('Could not connect to WebSocket server');
            self.switchTailing(false, false);
            setTimeout(function () {
                self.openSocket();
            }, 60000);
        };
    };

    this.closeSocket = function() {
        if (this.socket) {
            this.socket.close();
            this.socket = null;
        }
    };

    this.establish = function(tailers) {
        console.log(tailers);
        if (this.endpointEstablished) {
            this.logtails = this.endpointEstablished(this.endpoint, tailers);
            for (let key in tailers) {
                let tailer = tailers[key];
                let logtail = this.getLogtail(tailer.name);
                if (logtail) {
                    let self = this;
                    self.switchTailing(tailer.name, true);
                    logtail.closest(".logtail-content").find(".bite-tail").click(function () {
                        self.switchTailing(tailer.name);
                    });
                }
            }
        }
        if (this.establishCompleted) {
            this.establishCompleted();
            if (this.pendingMessages && this.pendingMessages.length > 0) {
                for (let key in this.pendingMessages) {
                    this.printEventMessage(this.pendingMessages[key]);
                }
                this.pendingMessages = null;
            }
        }
        this.established = true;
    };

    this.heartbeatPing = function() {
        if (this.heartbeatTimer) {
            clearTimeout(this.heartbeatTimer);
        }
        let self = this;
        this.heartbeatTimer = setTimeout(function () {
            if (self.socket) {
                self.socket.send("--heartbeat-ping--");
                self.heartbeatTimer = null;
                self.heartbeatPing();
            }
        }, 57000);
    };

    this.getLogtail = function(tailerName) {
        if (this.logtails && tailerName) {
            return this.logtails[tailerName];
        } else {
            return $(".logtail");
        }
    };

    this.switchTailing = function(tailerName, status) {
        let logtail = this.getLogtail(tailerName);
        if (status !== true && status !== false) {
            status = !logtail.data("tailing");
        }
        if (status) {
            logtail.data("tailing", true);
            logtail.closest(".logtail-content").find(".tail-status").addClass("active");
            this.scrollToBottom(logtail);
        } else {
            logtail.data("tailing", false);
            logtail.closest(".logtail-content").find(".tail-status").removeClass("active");
        }
    };

    this.scrollToBottom = function(logtail) {
        if (logtail.data("tailing")) {
            let timer = logtail.data("timer");
            if (timer) {
                clearTimeout(timer);
            }
            timer = setTimeout(function () {
                logtail.scrollTop(logtail.prop("scrollHeight"));
                if (logtail.find("p").length > 11000) {
                    logtail.find("p:gt(10000)").remove();
                }
            }, 300);
            logtail.data("timer", timer);
        }
    };

    this.refresh = function(logtail) {
        if (logtail) {
            this.scrollToBottom(logtail);
        } else {
            for (let key in this.logtails) {
                this.scrollToBottom(this.logtails[key]);
            }
        }
    };

    this.printMessage = function(tailerName, text) {
        let logtail = this.getLogtail(tailerName);
        this.visualize(logtail, text);
        this.indicate(logtail);
        let line = $("<p/>").text(text);
        logtail.append(line);
        this.scrollToBottom(logtail);
    };

    this.printEventMessage = function(text, tailerName) {
        if (tailerName) {
            let logtail = this.getLogtail(tailerName);
            $("<p/>").addClass("event").html(text).appendTo(logtail);
            this.scrollToBottom(logtail);
        } else {
            for (let key in this.logtails) {
                this.printEventMessage(text, key);
            }
        }
    };

    this.printErrorMessage = function(text, tailerName) {
        if (tailerName) {
            let logtail = this.getLogtail(tailerName);
            $("<p/>").addClass("event error").html(text).appendTo(logtail);
            this.scrollToBottom(logtail);
        } else {
            for (let key in this.logtails) {
                this.printErrorMessage(text, key);
            }
        }
    };

    this.indicate = function(logtail) {
        let endpointIndex = logtail.data("endpoint-index");
        let logtailIndex = logtail.data("logtail-index");
        setTimeout(function() {
            let indicator1 = $(".endpoints.tabs .tabs-title.available .indicator").eq(endpointIndex);
            if (!indicator1.hasClass("on")) {
                indicator1.addClass("blink on");
                setTimeout(function () {
                    indicator1.removeClass("blink on");
                }, 500);
            }
            let indicator2 = $(".logtails.tabs .tabs-title.available .indicator").eq(logtailIndex);
            if (!indicator2.hasClass("on")) {
                indicator2.addClass("blink on");
                setTimeout(function () {
                    indicator2.removeClass("blink on");
                }, 500);
            }
        }, 2);
    };

    this.visualize = function(logtail, text) {
        let self = this;
        setTimeout(function() {
            let missileTrack = logtail.closest(".logtail-content").find(".missile-track");
            self.launchMissile(missileTrack, text);
        }, 1);
    };

    // A function for visualizing Aspectran app logs
    const pattern1 = /^Session ([\w\.]+) complete, active requests=(\d+)/i;
    const pattern2 = /^Session ([\w\.]+) deleted in session data store/i;
    const pattern3 = /^Session ([\w\.]+) accessed, stopping timer, active requests=(\d+)/i;
    const pattern4 = /^Creating new session id=([\w\.]+)/i;
    this.launchMissile = function(missileTrack, text) {
        let idx = text.indexOf("] ");
        if (idx !== -1) {
            text = text.substring(idx + 2);
        }

        let sessionId = "";
        let requests = 0;
        if (pattern1.test(text) || pattern2.test(text)) {
            sessionId = RegExp.$1;
            requests = RegExp.$2||0;
            if (requests > 3) {
                requests = 3;
            }
            requests++;
            let mis = missileTrack.find(".missile[sessionId='" + (sessionId + requests) + "']");
            if (mis.length > 0) {
                let dur = 850;
                if (mis.hasClass("mis-2")) {
                    dur += 250;
                } else if (mis.hasClass("mis-3")) {
                    dur += 500;
                }
                setTimeout(function () {
                    mis.remove();
                }, dur);
            }
            return;
        }
        if (pattern3.test(text) || pattern4.test(text)) {
            sessionId = RegExp.$1;
            requests = RegExp.$2||1;
            if (requests > 3) {
                requests = 3;
            }
        }
        if (requests > 0) {
            let min = 3;
            let max = 90 - (requests * 2);
            let top = (Math.floor(Math.random() * (max - min + 1)) + min) + '%';
            $("<div/>")
                .attr("sessionId", sessionId + requests)
                .css("top", top)
                .addClass("missile mis-" + requests)
                .appendTo(missileTrack);
        }
    };
}