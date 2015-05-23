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

namespace GeoAdServer.WebApi.Controllers
{
    public class DashboardController : Controller
    {
        public ActionResult Home()
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;

            if (IsAdmin)

            {
                ViewBag.Name = "Super User";
            }
            else
            {
                ViewBag.Name = User.Identity.Name;
            }
            
            return View();
        }

        [HttpGet]
        public ActionResult NewLocation()
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;

            var NewLocation = new LocationApiModel();

            return View(NewLocation);
        }

        [HttpPost]
        public ActionResult NewLocation([Bind]LocationApiModel location)
        {
            if (ModelState.IsValid)
            {
                var UserId = User.Identity.GetUserId();
                var ToAdd = location.CreateLocationFromModel(UserId, DataRepos.Locations);

                DataRepos.Locations.Insert(ToAdd);
            }

            return RedirectToAction("Home");
        }

        public ActionResult ChangePassword()
        {
            return View();
        }

        public ActionResult ManageLocations()
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;

            var UserId = User.Identity.GetUserId();
            ViewBag.UserId = UserId;

            IEnumerable<LocationDTO> vData = DataRepos.Locations./*GetByUserId(UserId)*/GetAll();

            return View(vData);
        }

        public ActionResult ManageOffers(int LocationId, string LocationName)
        {
            IEnumerable<OfferingDTO> vData = DataRepos.Offerings.GetByLocationId(LocationId);

            var UserId = User.Identity.GetUserId();
            ViewBag.UserId = UserId;

            ViewBag.LocId = LocationId;
            ViewBag.LocName = LocationName;

            return View(vData);
        }

        public ActionResult EditLocation(int Id)
        {
            var IsAdmin = User.Identity.Name.CompareTo("admin@geoad.com") == 0;
            ViewBag.IsAdmin = IsAdmin;

            LocationDTO vData = DataRepos.Locations.GetById(Id);

            return View(vData);
        }
    }
}
