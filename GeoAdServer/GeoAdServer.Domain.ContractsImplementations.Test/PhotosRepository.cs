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
            string connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";

            var container = new WindsorContainer();
            container.Register(
                Component.For<ILocationsRepository>()
                         .ImplementedBy<PostgresqlLocationsRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrslocrepo"));
            container.Register(
                Component.For<IPhotosRepository>()
                         .ImplementedBy<PostgresqlPhotosRepository>()
                         .DependsOn(Dependency.OnValue("connectionString", connectionString))
                         .Named("pgrsphorepo"));

            //Test
            var locationsRepository = container.Resolve<ILocationsRepository>();
            var photosRepository = container.Resolve<IPhotosRepository>();

            var locationEnum = locationsRepository.GetAll().GetEnumerator();
            locationEnum.MoveNext();
            int locationId = locationEnum.Current.Id;
            byte[] data = new byte[] {1, 2, 4};

            var photo = new Photo
            {
                LocationId = locationId,
                Data = data,
                Width = 3,
                Height = 1
            };
            int id = photosRepository.Insert(photo);
            var photoDTO = photosRepository.GetById(id);
            var photoDTOData = photosRepository.GetPhotoData(id);
            Assert.IsTrue(photo.Data.Length == photoDTOData.Length);
            for (int i = 0; i < photo.Data.Length; i++) Assert.IsTrue(photo.Data[i] == photoDTOData[i]);
            var photos = photosRepository.GetByLocationId(locationId);
            Assert.IsTrue(new List<PhotoDTO>(photos).Contains(photoDTO));
            Assert.IsTrue(photosRepository.Delete(id + 1));
        }
    }
}
