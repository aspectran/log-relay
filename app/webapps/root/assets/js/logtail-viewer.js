function LogtailViewer(endpoint, endpointEstablished, establishCompleted) {
    this.endpoint = endpoint;
    this.endpointEstablished = endpointEstablished;
    this.establishCompleted = establishCompleted;
    this.socket = null;
    this.heartbeatTimer = null;
    this.pendingMessages = [];
    this.logtails = {};
    this.missileTracks = {};
    this.indicators = {};
    this.established = false;

    this.openSocket = function () {
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
                        let logtailName = msg.substring(0, idx);
                        self.printMessage(logtailName, msg.substring(idx + 1));
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
            setTimeout(function () {
                self.openSocket();
            }, 60000);
        };
    };

    this.closeSocket = function () {
        if (this.socket) {
            this.socket.close();
            this.socket = null;
        }
    };

    this.establish = function (tailers) {
        this.endpoint['viewer'] = this;
        if (this.endpointEstablished) {
            this.endpointEstablished(this.endpoint, tailers);
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

    this.heartbeatPing = function () {
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

    this.getLogtail = function (logtailName) {
        if (this.logtails && logtailName) {
            return this.logtails[logtailName];
        } else {
            return $(".logtail");
        }
    };

    this.scrollToBottom = function (logtail) {
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

    this.refresh = function (logtail) {
        if (logtail) {
            this.scrollToBottom(logtail);
        } else {
            for (let key in this.logtails) {
                this.scrollToBottom(this.logtails[key]);
            }
        }
    };

    this.printMessage = function (logtailName, text) {
        this.indicate(logtailName);
        this.visualize(logtailName, text);
        let logtail = this.getLogtail(logtailName);
        $("<p/>").text(text).appendTo(logtail);
        this.scrollToBottom(logtail);
    };

    this.printEventMessage = function (text, logtailName) {
        if (logtailName) {
            let logtail = this.getLogtail(logtailName);
            $("<p/>").addClass("event ellipses").html(text).appendTo(logtail);
            this.scrollToBottom(logtail);
        } else {
            for (let key in this.logtails) {
                this.printEventMessage(text, key);
            }
        }
    };

    this.printErrorMessage = function (text, logtailName) {
        if (logtailName) {
            let logtail = this.getLogtail(logtailName);
            $("<p/>").addClass("event error").html(text).appendTo(logtail);
            this.scrollToBottom(logtail);
        } else {
            for (let key in this.logtails) {
                this.printErrorMessage(text, key);
            }
        }
    };

    this.indicate = function (logtailName) {
        let indicators = this.indicators[logtailName];
        if (indicators) {
            for (let key in indicators) {
                let indicator = indicators[key];
                if (!indicator.hasClass("on")) {
                    indicator.addClass("blink on");
                    setTimeout(function () {
                        indicator.removeClass("blink on");
                    }, 500);
                }
            }
        }
    };

    this.visualize = function (logtailName, text) {
        let missileTrack = this.missileTracks[logtailName];
        if (missileTrack) {
            let self = this;
            setTimeout(function () {
                let launcher = missileTrack.data("launcher");
                let launchMissile = self[launcher];
                if (launchMissile) {
                    launchMissile(missileTrack, text);
                }
            }, 1);
        }
    };

    // A function for visualizing Aspectran app logs
    const pattern1 = /^Session ([\w\.]+) complete, active requests=(\d+)/i;
    const pattern2 = /^Session ([\w\.]+) deleted in session data store/i;
    const pattern3 = /^Session ([\w\.]+) accessed, stopping timer, active requests=(\d+)/i;
    const pattern4 = /^Creating new session id=([\w\.]+)/i;
    this.missileLaunch1 = function (missileTrack, text) {
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