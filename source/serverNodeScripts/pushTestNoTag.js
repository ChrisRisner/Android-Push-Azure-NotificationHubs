exports.get = function(request, response) {
    var azure = require('azure');
    var notificationHubService = azure.createNotificationHubService('NotificationHubName', 
                        'NotificationHubFullSharedAccessSignature');
    notificationHubService.gcm.send(null,
            '{"data":{"msg" : "Hello from Mobile Services!"}}'
        ,
        function (error)
        {
            if (!error) {
                console.warn("Notification successful");
            }
        }
    );
    response.send(statusCodes.OK, { message : 'Notification Sent' });
};