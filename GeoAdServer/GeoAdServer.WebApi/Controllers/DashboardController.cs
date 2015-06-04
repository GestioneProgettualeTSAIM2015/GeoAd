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

namespace GeoAdServer.WebApi.Controllers
{
    [RedirAuthorize(RedirectToController="Login")]
    public class DashboardController : Controller
    {
        public ActionResult Home()
        {
            var isAdmin = IsAdmin();
            ViewBag.IsAdmin = IsAdmin();

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
            ViewBag.IsAdmin = IsAdmin();
            return View();
        }

        public ActionResult ChangePassword()
        {
            if (IsAdmin())
            {
                ViewBag.Title = "Unauthorize";
                ViewBag.Message = "You don't have permissions to see this page";
                return View("~/Views/Dashboard/Error.cshtml");
            }

            return View();
        }

        public ActionResult ManageLocations()
        {
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                ViewBag.IsAdmin = IsAdmin();

                var UserId = User.Identity.GetUserId();
                ViewBag.UserId = UserId;

                IEnumerable<LocationDTO> vData = repo.GetByUserId(UserId);

                return View(vData);
            }
        }

        public ActionResult ManageOffers(int Id)
        {
            if (IsAdmin())
            {
                ViewBag.Title = "Unauthorize";
                ViewBag.Message = "You don't have permissions to see this page";
                return View("~/Views/Dashboard/Error.cshtml");
            }

            using (IOfferingsRepository repo = DataRepos.Offerings)
            {
                using (ILocationsRepository repo2 = DataRepos.Locations)
                {
                    IEnumerable<OfferingDTO> vData = repo.GetByLocationId(Id);

                    ViewBag.LocId = Id;
                    ViewBag.LocName = repo2.GetById(Id).Name;

                    return View(vData);
                }
            }
        }

        public ActionResult ManagePhotos(int Id)
        {
            using (IPhotosRepository repo = DataRepos.Photos)
            {
                using (ILocationsRepository repo2 = DataRepos.Locations)
                {
                    IEnumerable<PhotoDTO> vData = repo.GetByLocationId(Id);

                    ViewBag.LocId = Id;
                    ViewBag.LocName = repo2.GetById(Id).Name;

                    return View(vData);
                }
            }
        }

        public ActionResult EditLocation(int Id)
        {
            ViewBag.IsAdmin = IsAdmin();

            LocationDTO vData = DataRepos.Locations.GetById(Id);
            return View(vData);
        }

        public ActionResult EditOffer([Bind] OfferingDTO offer)
        {
            if (IsAdmin())
            {
                ViewBag.Title = "Unauthorize";
                ViewBag.Message = "You don't have permissions to see this page";
                return View("~/Views/Dashboard/Error.cshtml");
            }

            ViewBag.Title = truncDesc(offer.Desc);
            return View(offer);
        }

        private bool IsAdmin()
        {
            return User.Identity.Name.CompareTo("admin@geoad.com") == 0;
        }

        private string truncDesc(string aDesc)
        {
            return aDesc.Length <= 20 ? aDesc : aDesc.Substring(0, 20) + "...";
        }
    }
}
