using GeoAdServer.Domain.Contracts;
using GeoAdServer.Domain.Entities.DTOs;
using GeoAdServer.Postgresql;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace GeoAdServer.WebApi.Controllers
{
    [Authorize]
    public class LocationsController : ApiController
    {
        private string _connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
        private ILocationsRepository _locationsRepository;

        public LocationsController() : base()
        {
            _locationsRepository = new PostgresqlLocationsRepository(_connectionString);
        }

        public IQueryable<LocationDTO> Get()
        {
            return _locationsRepository.GetAll().AsQueryable();
        }

        public IQueryable<LocationDTO> Get(int id)
        {
            return _locationsRepository.GetByUserId(id).AsQueryable();
        }
    }
}
