using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using GeoAdServer.WebApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Principal;
using System.Text;
using System.Threading.Tasks;
using System.Web.Http.Controllers;
using GeoAdServer.GeneralUtilities;
using System.Net;
using System.Net.Http;

namespace GeoAdServer.WebApi.Support
{
    public static class Utils
    {
        public static string GetUserId(this HttpRequestContext context)
        {
            return context.Principal.Identity.GetUserId();
        }

        public static string GetUserId(this IIdentity identity)
        {
            return identity.Name.GetHashCode().ToString();
        }

        public static bool IsLocationOwner(this int id, string userId)
        {
            using (var repos = DataRepos.Instance)
            {
                return id.IsLocationOwner(userId, repos.Locations);
            }
        }

        public static bool IsLocationOwner(this int id, string userId, ILocationsRepository repoloc)
        {
            var owner = repoloc.GetOwnerId(id);
            if (owner == null) return false;

            return owner == userId;
        }

        public static bool IsAdmin(this IIdentity identity)
        {
            return identity.Name.Equals("admin@geoad.com");
        }

        public static Location CreateLocationFromModel(this LocationApiModel model, string userId, ILocationsRepository repository, IIdentity identity)
        {
            int? sCatId = null;
            var pCat = repository.GetCategoryByName(model.PCat);
            if (pCat == null) throw new ArgumentException("primary category not found");

            if (model.SCat != null)
            {
                var sCat = repository.GetCategoryByName(model.SCat);
                if (sCat == null)
                {
                    sCatId = repository.InsertCategory(model.SCat, pCat.Id);
                }
                else if (!sCat.Aggregate.HasValue || sCat.Aggregate.Value != pCat.Id)
                {
                    var message = string.Format("categories aren't consistent, {0} is a ", sCat.Name);
                    if (sCat.Aggregate.HasValue)
                        message += string.Format("sub-category of {0}",
                            repository.GetCategoryById(sCat.Aggregate.Value).Name);
                    else message += "primary category";

                    throw new ArgumentException(message);
                }
            }

            var typeId = identity.IsAdmin() ? 1 : 0;

            return new Location
            {
                UserId = userId,
                PCatId = pCat.Id,
                SCatId = sCatId,
                Name = model.Name,
                Lat = model.Lat,
                Lng = model.Lng,
                Desc = model.Desc,
                Type = Types.Values[typeId]
            };
        }
    }
}
