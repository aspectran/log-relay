function AppmonPollingClient(endpoint, onEndpointJoined, onEstablishCompleted) {

    this.start = function () {
        join();
    };

    this.speed = function (speed) {
        changePollingInterval(speed);
    };

    const join = function () {
        $.ajax({
            url: endpoint.basePath + "appmon/endpoint/join",
            type: 'post',
            dataType: "json",
            success: function (data) {
                if (data) {
                    endpoint['mode'] = "polling";
                    endpoint['pollingInterval'] = data.pollingInterval;
                    if (onEndpointJoined) {
                        onEndpointJoined(endpoint, data);
                    }
                    if (onEstablishCompleted) {
                        onEstablishCompleted(endpoint, data);
                    }
                    endpoint.viewer.printEventMessage("Polling every " + data.pollingInterval + " milliseconds.");
                    polling();
                }
            }
        });
    };

    const rejoin = function () {
        endpoint.viewer.printErrorMessage("Connection lost. It will retry in 5 seconds.");
        setTimeout(function () {
            location.reload();
        }, 5000)
    };

    const polling = function () {
        $.ajax({
            url: endpoint.basePath + "appmon/endpoint/pull",
            type: 'get',
            success: function (data) {
                if (data) {
                    for (let key in data) {
                        endpoint.viewer.printMessage(data[key]);
                    }
                    setTimeout(polling, endpoint.pollingInterval);
                } else {
                    rejoin();
                }
            }
        });
    };

    const changePollingInterval = function (speed) {
        $.ajax({
            url: endpoint.basePath + "appmon/endpoint/pollingInterval",
            type: 'post',
            dataType: "json",
            data: {
                speed: speed
            },
            success: function (data) {
                console.log("pollingInterval", data);
                if (data) {
                    endpoint.pollingInterval = data;
                    endpoint.viewer.printEventMessage("Polling every " + data + " milliseconds.");
                }
            }
        });
    };

}
