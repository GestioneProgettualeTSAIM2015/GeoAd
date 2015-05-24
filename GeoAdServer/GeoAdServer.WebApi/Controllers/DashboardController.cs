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
            var isAdmin = IsAdmin();
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

        [HttpGet]
        public ActionResult NewLocation()
        {
            ViewBag.IsAdmin = IsAdmin();

            return View();
        }

        public ActionResult ChangePassword()
        {
            return View();
        }

        public ActionResult ManageLocations()
        {
            ViewBag.IsAdmin = IsAdmin();

            var UserId = User.Identity.GetUserId();
            ViewBag.UserId = UserId;

            IEnumerable<LocationDTO> vData = DataRepos.Locations./*GetByUserId(UserId)*/GetAll();

            return View(vData);
        }

        public ActionResult ManageOffers(int Id, string LocationName)
        {
            IEnumerable<OfferingDTO> vData = DataRepos.Offerings.GetByLocationId(Id);

            ViewBag.LocId = Id;
            ViewBag.LocName = LocationName;

            return View(vData);
        }

        public ActionResult EditLocation(int Id)
        {
            ViewBag.IsAdmin = IsAdmin();

            LocationDTO vData = DataRepos.Locations.GetById(Id);

            return View(vData);
        }

        public bool IsAdmin()
        {
            return User.Identity.Name.CompareTo("admin@geoad.com") == 0;
        }
    }
}
