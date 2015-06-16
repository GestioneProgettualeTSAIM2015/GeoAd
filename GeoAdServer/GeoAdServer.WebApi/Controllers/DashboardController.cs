using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Mvc;
using GeoAdServer.WebApi.Support;
using GeoAdServer.Domain.Entities;
using GeoAdServer.WebApi.Models;
using GeoAdServer.Domain.Contracts;
using System.Dynamic;

namespace GeoAdServer.WebApi.Controllers
{
    [RedirAuthorize(RedirectToController="Login")]
    public class DashboardController : Controller
    {
        public ActionResult Home()
        {
            var isAdmin = User.Identity.IsAdmin();
            ViewBag.IsAdmin = isAdmin;

            if (isAdmin)
            {
                ViewBag.Name = "Super User";
            }
            else
            {
                ViewBag.Name = User.Identity.Name;
            }
            
            return View();
        }

        public ActionResult NewLocation()
        {
            ViewBag.IsAdmin = User.Identity.IsAdmin();
            using (var repos = DataRepos.Instance)
            {
                return View(repos.Locations.GetCategories());
            }
        }

        public ActionResult ChangePassword()
        {
            if (User.Identity.IsAdmin())
            {
                return NewErrorView();
            }

            return View();
        }

        public ActionResult ManageLocations()
        {
            using (var repos = DataRepos.Instance)
            {
                ViewBag.IsAdmin = User.Identity.IsAdmin();

                var UserId = User.Identity.GetUserId();
                ViewBag.UserId = UserId;

                IEnumerable<LocationDTO> locations = repos.Locations.GetByUserId(UserId);

                return View(locations);
            }
        }

        public ActionResult ManageOffers(int Id)
        {
            if (User.Identity.IsAdmin())
            {
                return NewErrorView();
            }

            using (var repos = DataRepos.Instance)
            {
                IEnumerable<OfferingDTO> offerings = repos.Offerings.GetByLocationId(Id);

                ViewBag.LocId = Id;
                ViewBag.LocName = repos.Locations.GetById(Id).Name;

                return View(offerings);
            }
        }

        public ActionResult ManagePhotos(int Id)
        {
            using (var repos = DataRepos.Instance)
            {
                IEnumerable<PhotoDTO> photos = repos.Photos.GetByLocationId(Id);

                ViewBag.LocId = Id;
                ViewBag.LocName = repos.Locations.GetById(Id).Name;

                return View(photos);
            }
        }

        public ActionResult EditLocation(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                ViewBag.IsAdmin = User.Identity.IsAdmin();

                dynamic models = new ExpandoObject();
                models.CategoriesMap = repos.Locations.GetCategories();
                models.Location = repos.Locations.GetById(id);

                return View(models);
            }
        }

        public ActionResult EditOffer(int id)
        {
            if (User.Identity.IsAdmin())
            {
                return NewErrorView();
            }

            using (var repos = DataRepos.Instance)
            {
                var offer = repos.Offerings.GetById(id);
                ViewBag.Title = truncDesc(offer.Desc);
                return View(offer);
            }
        }

        private string truncDesc(string aDesc)
        {
            return aDesc.Length <= 20 ? aDesc : aDesc.Substring(0, 20) + "...";
        }

        public ViewResult NewErrorView()
        {
            ViewBag.Title = "Unauthorize";
            ViewBag.Message = "You don't have permissions to see this page";
            return View("~/Views/Dashboard/Error.cshtml");
        }
    }
}
