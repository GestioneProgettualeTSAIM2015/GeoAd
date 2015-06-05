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
            return View();
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
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                ViewBag.IsAdmin = User.Identity.IsAdmin();

                var UserId = User.Identity.GetUserId();
                ViewBag.UserId = UserId;

                IEnumerable<LocationDTO> vData = repo.GetByUserId(UserId);

                return View(vData);
            }
        }

        public ActionResult ManageOffers(int Id)
        {
            if (User.Identity.IsAdmin())
            {
                return NewErrorView();
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
            using (ILocationsRepository repo = DataRepos.Locations)
            {
                ViewBag.IsAdmin = User.Identity.IsAdmin();

                LocationDTO vData = repo.GetById(Id);
                return View(vData);
            }
        }

        public ActionResult EditOffer([Bind] OfferingDTO offer)
        {
            if (User.Identity.IsAdmin())
            {
                return NewErrorView();
            }

            ViewBag.Title = truncDesc(offer.Desc);
            return View(offer);
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
