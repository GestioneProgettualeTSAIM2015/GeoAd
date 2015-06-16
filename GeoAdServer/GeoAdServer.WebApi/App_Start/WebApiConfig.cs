using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Web.Http;
using Microsoft.Owin.Security.OAuth;
using Newtonsoft.Json.Serialization;
using System.Web.Http.OData.Builder;
using System.Web.Http.OData.Extensions;
using GeoAdServer.Domain.Entities.DTOs;
using System.Web.Http.Routing;

namespace GeoAdServer.WebApi
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            // Web API configuration and services
            // Configure Web API to use only bearer token authentication.
            config.SuppressDefaultHostAuthentication();
            config.Filters.Add(new HostAuthenticationFilter(OAuthDefaults.AuthenticationType));

            // Web API routes
            config.MapHttpAttributeRoutes();

            config.Routes.MapHttpRoute(
                name: "PhotoData",
                routeTemplate: "api/{controller}/data/{photoId}"
            );

            config.Routes.MapHttpRoute(
                name: "GetAllWithPosition",
                routeTemplate: "api/{controller}/{pos}",
                defaults: new { controller = "positions" },
                constraints: new { httpMethod = new HttpMethodConstraint(HttpMethod.Get) }
            );

            config.Routes.MapHttpRoute(
                name: "MapKey",
                routeTemplate: "api/{controller}/{action}",
                defaults: new { controller = "locations" },
                constraints: new { httpMethod = new HttpMethodConstraint(HttpMethod.Post) }
            );

            config.Routes.MapHttpRoute(
                name: "FromUserApi",
                routeTemplate: "api/{controller}/fromuser/{userId}"
            );

            config.Routes.MapHttpRoute(
                name: "FromLocationApi",
                routeTemplate: "api/{controller}/fromlocation/{locationId}"
            );

            config.Routes.MapHttpRoute(
                name: "DefaultRoute",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );

            config.AddODataQueryFilter();
        }
    }
}
