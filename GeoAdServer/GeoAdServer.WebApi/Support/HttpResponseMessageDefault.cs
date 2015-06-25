using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.WebApi.Support
{
    public static class HttpResponseMessageDefault
    {
        public static HttpResponseMessage CreateResponseForUnprocessableEntity(this HttpRequestMessage request, string messageContent = null)
        {
            var message = "Unprocessable Entity";
            if (messageContent != null) message += ": " + messageContent;
            return request.CreateResponse((HttpStatusCode)422, message);
        }

        public static HttpResponseMessage CreateResponseForInvalidModelState(this HttpRequestMessage request)
        {
            return request.CreateResponseForUnprocessableEntity("invalid model state");
        }

        public static HttpResponseMessage CreateResponseForNotOwner(this HttpRequestMessage request)
        {
            return request.CreateResponse(HttpStatusCode.Unauthorized, "Only the location's owner can execute this action");
        }
    }
}
