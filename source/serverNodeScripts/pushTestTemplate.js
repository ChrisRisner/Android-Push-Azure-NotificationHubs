exports.get = function(request, response) {
    var azure = require('azure');
    var notificationHubService = azure.createNotificationHubService('NotificationHubName', 
                        'NotificationHubFullSharedAccessSignature');
    var payload = '{ "message" : "Template push to everyone!", "collapse_key" : "Message" }';
    notificationHubService.send(null, payload, 
     function(error, outcome) {
         console.log('issue sending push');
         console.log('error: ', error);
         console.log('outcome: ',outcome);
     });  
    response.send(statusCodes.OK, { message : 'Notification Sent' });
};