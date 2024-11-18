function AppmonViewer() {
    let logtails = {};
    let missileTracks = {};
    let indicators = {};
    let statuses = {};
    let visible = false;
    let prevLogTime = null;
    let prevSentTime = new Date().getTime();
    let prevPosition = 0;

    this.setLogtail = function (name, logtail) {
        logtails[name] = logtail;
    };

    this.refresh = function (logtail) {
        if (logtail) {
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                scrollToBottom(logtails[key]);
            }
        }
    };

    this.clear = function (logtail) {
        if (logtail) {
            logtail.empty();
        }
    };

    this.setMissileTrack = function (name, missileTrack) {
        missileTracks[name] = (missileTrack.length > 0 ? missileTrack : null);
    };

    this.setIndicators = function (name, indicatorArr) {
        indicators[name] = indicatorArr;
    };

    this.setStatus = function (name, status) {
        statuses[name] = status;
    };

    this.setVisible = function (flag) {
        visible = !!flag;
        if (!visible) {
            for (let key in missileTracks) {
                missileTracks[key].empty();
            }
        }
    };

    this.getLogtails = function () {
        return logtails;
    };

    const getLogtail = function (name) {
        if (logtails && name) {
            return logtails[name];
        } else {
            return $(".logtail");
        }
    };

    const getStatus = function (name) {
        if (statuses && name) {
            return statuses[name];
        } else {
            return $(".status-box");
        }
    };

    const scrollToBottom = function (logtail) {
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

    this.printMessage = function (msg) {
        let idx = msg.indexOf(":");
        let name = msg.substring(0, idx);
        let text = msg.substring(idx + 1);
        if (text.startsWith("logtail:")) {
            text = text.substring(8);
            if (text.startsWith("last:")) {
                text = text.substring(5);
                printLog(name, text, false);
            } else {
                printLog(name, text, true);
            }
        } else if (text.startsWith("status:")) {
            text = text.substring(7);
            idx = text.indexOf(":");
            if (idx !== -1) {
                let label = text.substring(0, idx);
                let data = JSON.parse(text.substring(idx + 1));
                console.log(name, label, data);
                printStatus(name, label, data);
            }
        }
    };

    const printLog = function (name, text, visualizing) {
        indicate(name);
        let logtail = getLogtail(name);
        if (!logtail.data("pause")) {
            if (visualizing) {
                visualize(name, text);
            }
            $("<p/>").text(text).appendTo(logtail);
            scrollToBottom(logtail);
        }
    };

    this.printEventMessage = function (text, name) {
        if (name) {
            let logtail = getLogtail(name);
            $("<p/>").addClass("event ellipses").html(text).appendTo(logtail);
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                this.printEventMessage(text, key);
            }
        }
    };

    this.printErrorMessage = function (text, name) {
        if (name || !Object.keys(logtails).length) {
            let logtail = getLogtail(name);
            $("<p/>").addClass("event error").html(text).appendTo(logtail);
            scrollToBottom(logtail);
        } else {
            for (let key in logtails) {
                this.printErrorMessage(text, key);
            }
        }
    };

    const indicate = function (name) {
        let indicatorArr = indicators[name];
        if (indicatorArr) {
            let first = true;
            for (let key in indicatorArr) {
                if (first || visible) {
                    let indicator = indicatorArr[key];
                    if (!indicator.hasClass("on")) {
                        indicator.addClass("blink on");
                        setTimeout(function () {
                            indicator.removeClass("blink on");
                        }, 500);
                    }
                }
                first = false;
            }
        }
    };

    const visualize = function (name, text) {
        if (visible) {
            let missileTrack = missileTracks[name];
            if (missileTrack) {
                setTimeout(function () {
                    let visualizing = missileTrack.data("visualizing");
                    if (visualizing) {
                        launchMissile(missileTrack, text);
                    }
                }, 1);
            }
        }
    };

    const pattern1 = /^DEBUG (.+) \[(.+)] Create new session id=([^\s;]+)/;
    const pattern2 = /^DEBUG (.+) \[(.+)] Session ([^\s;]+) accessed, stopping timer, active requests=(\d+)/;
    const pattern3 = /^DEBUG (.+) \[(.+)] Session ([^\s;]+) complete, active requests=(\d+)/;
    const pattern4 = /^DEBUG (.+) \[(.+)] Invalidate session id=([^\s;]+)/;
    const pattern5 = /^DEBUG (.+) \[(.+)] Reject session id=([^\s;]+)/;

    const launchMissile = function (missileTrack, text) {
        let matches1 = pattern1.exec(text);
        let matches2 = pattern2.exec(text);
        let matches3 = pattern3.exec(text);
        let matches4 = pattern4.exec(text);
        let matches5 = pattern5.exec(text);

        // if (matches1 || matches2 || matches3 || matches4 || matches5) {
        //     console.log(text);
        //     console.log('matches1', matches1);
        //     console.log('matches2', matches2);
        //     console.log('matches3', matches3);
        //     console.log('matches4', matches4);
        //     console.log('matches5', matches5);
        // }

        let dateTime = "";
        let sessionId = "";
        let requests = 0;
        let delay = 0;
        if (matches3 || matches4 || matches5) {
            if (matches3) {
                sessionId = matches3[3];
                requests = parseInt(matches3[4]) + 1;
            } else if (matches4) {
                sessionId = matches4[3];
                requests = 1
            } else if (matches5) {
                sessionId = matches5[3];
                requests = 1
            }
            if (requests > 3) {
                requests = 3;
            }
            let mis = missileTrack.find(".missile[sessionId='" + sessionId + "_" + requests + "']");
            if (mis.length > 0) {
                let dur = 650 + mis.data("delay")||0;
                if (mis.hasClass("mis-2")) {
                    dur += 250;
                } else if (mis.hasClass("mis-3")) {
                    dur += 500;
                }
                setTimeout(function () {
                    mis.remove();
                }, dur + 800);
            }
            return;
        }
        if (matches1 || matches2) {
            if (matches1) {
                dateTime = matches1[1];
                sessionId = matches1[3];
                requests = 1;
            } else if (matches2) {
                dateTime = matches2[1];
                sessionId = matches2[3];
                requests = matches2[4];
            }
            if (requests > 3) {
                requests = 3;
            }
            let logTime = moment(dateTime);
            let currTime = new Date().getTime();
            let spentTime = currTime - prevSentTime;
            if (prevLogTime) {
                delay = logTime.diff(prevLogTime);
                if (delay >= 1000 || delay < 0 || spentTime >= delay + 1000) {
                    delay = 0;
                }
            }
            prevLogTime = logTime;
            prevSentTime = currTime;
        }
        if (requests > 0) {
            let position = generateRandom(3, 120 - 3 - (requests * 4 + 8));
            if (delay < 1000 && prevPosition) {
                if (Math.abs(position - prevPosition) <= 20) {
                    position = generateRandom(3, 120 - 3 - (requests * 4 + 8));
                    if (Math.abs(position - prevPosition) <= 20) {
                        position = generateRandom(3, 120 - 3 - (requests * 4 + 8));
                    }
                }
            }
            prevPosition = position;
            let mis = $("<div/>").attr("sessionId", sessionId + "_" + requests);
            mis.data("delay", 1000 + delay);
            setTimeout(function () {
                mis.addClass("mis-" + requests).removeClass("hidden");
            }, 1000 + delay);
            mis.css("top", position + "px");
            mis.appendTo(missileTrack).addClass("hidden missile");
        }
    };

    const generateRandom = function (min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    const printStatus = function (name, label, data) {
        switch (label) {
            case "session":
                printSessionStatus(name, data);
        }
    };

    const printSessionStatus = function (name, data) {
        let status = getStatus(name);
        status.find(".activeSessionCount").text(data.activeSessionCount);
        status.find(".highestActiveSessionCount").text(data.highestActiveSessionCount);
        status.find(".evictedSessionCount").text(data.evictedSessionCount);
        status.find(".createdSessionCount").text(data.createdSessionCount);
        status.find(".expiredSessionCount").text(data.expiredSessionCount);
        status.find(".rejectedSessionCount").text(data.rejectedSessionCount);
        status.find(".elapsed").text(data.elapsedTime);
        if (data.currentSessions) {
            status.find("ul.sessions").empty();
            data.currentSessions.forEach(function (username) {
                let indicator = $("<div/>").addClass("indicator");
                if (username.indexOf("0:") === 0) {
                    indicator.addClass("logged-out")
                }
                username = username.substring(2);
                let name = $("<span/>").addClass("name").text(username);
                let li = $("<li/>").append(indicator).append(name);
                status.find("ul.sessions").append(li);
            });
        }
    };
}
