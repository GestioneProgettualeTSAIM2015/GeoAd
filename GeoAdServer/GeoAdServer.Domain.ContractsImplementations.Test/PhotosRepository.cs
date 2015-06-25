using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Castle.Windsor;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Postgresql;
using Castle.MicroKernel.Registration;
using GeoAdServer.Domain.Entities;
using System.Collections.Generic;
using GeoAdServer.Domain.Entities.DTOs;

namespace GeoAdServer.Domain.ContractsImplementations.Test
{
    [TestClass]
    public class PhotosRepository
    {
        [TestMethod]
        public void PhotosRepositoryTest()
        {
            //Container initialization
            string connectionString = "";

            var container = new WindsorContainer();
            container.Register(
                Component.For<ILocationsRepository>()
                         .ImplementedBy<PostgresqLocationsRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrslocrepo"));
            container.Register(
                Component.For<IPhotosRepository>()
                         .ImplementedBy<PostgresqPhotosRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrsphorepo"));

            //Test
            var locationsRepository = container.Resolve<ILocationsRepository>();
            var photosRepository = container.Resolve<IPhotosRepository>();

            var locationEnum = locationsRepository.GetAll().GetEnumerator();
            locationEnum.MoveNext();
            int locationId = locationEnum.Current.Id;
            string data = "abc";

            var photo = new Photo
            {
                LocationId = locationId,
                Base64Thumbnail = data,
                Width = 2,
                Height = 1
            };
            int id = photosRepository.Insert(photo);
            var photoDTO = photosRepository.GetById(id);
            Assert.IsTrue(photo.Base64Thumbnail.Length == photoDTO.Base64Thumbnail.Length);
            Assert.AreEqual(photo.Base64Thumbnail, photoDTO.Base64Thumbnail);
            var photos = photosRepository.GetByLocationId(locationId);
            Assert.IsTrue(new List<PhotoDTO>(photos).Contains(photoDTO));
            Assert.IsTrue(photosRepository.Delete(id + 1));
        }
    }
}
