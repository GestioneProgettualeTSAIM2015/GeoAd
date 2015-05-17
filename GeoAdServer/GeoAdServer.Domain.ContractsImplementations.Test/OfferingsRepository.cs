using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Castle.Windsor;
using GeoAdServer.Domain.Contracts;
using Castle.MicroKernel.Registration;
using GeoAdServer.Postgresql;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System.Linq;

namespace GeoAdServer.Domain.ContractsImplementations.Test
{
    [TestClass]
    public class OfferingsRepository
    {
        [TestMethod]
        public void OfferingsRepositoryTest()
        {
            //Container initialization
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";

            var container = new WindsorContainer();
            container.Register(
                Component.For<ILocationsRepository>()
                         .ImplementedBy<PostgresqlLocationsRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrslocrepo"));
            container.Register(
                Component.For<IOfferingsRepository>()
                         .ImplementedBy<PostgresqlOfferingsRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrsoffrepo"));

            //Test
            var locationsRepository = container.Resolve<ILocationsRepository>();
            var offeringsRepository = container.Resolve<IOfferingsRepository>();

            var locationEnum = locationsRepository.GetAll().GetEnumerator();
            locationEnum.MoveNext();
            int locationId = locationEnum.Current.Id;

            var offering = new Offering
            {
                LocationId = locationId,
                Desc = "Test offerings",
                ExpDateMillis = (long) (DateTime.Now.AddHours(24) - new DateTime(1970, 1, 1)).TotalMilliseconds
            };
            int id = offeringsRepository.Insert(offering);
            var offeringDTO = offeringsRepository.GetByLocationId(locationId).Where(off => off.Id == id).Single();
            var copy = offeringsRepository.GetAll().Where(off => off.Equals(offeringDTO)).Single();
            offering.Desc = "new desc";
            Assert.IsTrue(offeringsRepository.Update(id, offering));
            Assert.IsFalse(offeringsRepository.Update(-1, offering));
            Assert.IsNull(offeringsRepository.GetAll().Where(off => off.Equals(offeringDTO)).SingleOrDefault());
            Assert.IsTrue(offeringsRepository.DeleteById(id));
            Assert.IsNull(offeringsRepository.GetByLocationId(locationId).Where(off => off.Id == id).SingleOrDefault());
        }
    }
}
