using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ModelBinding;
using System.Web.Http.OData;
using System.Web.Http.OData.Query;
using System.Web.Http.OData.Routing;
using GeoAdServer.Domain.Entities.DTOs;
using Microsoft.Data.OData;
using GeoAdServer.Domain.Contracts;
using GeoAdServer.Postgresql;

namespace GeoAdServer.WebApi.Controllers
{
    /*
    The WebApiConfig class may require additional changes to add a route for this controller. Merge these statements into the Register method of the WebApiConfig class as applicable. Note that OData URLs are case sensitive.

    using System.Web.Http.OData.Builder;
    using System.Web.Http.OData.Extensions;
    using GeoAdServer.Domain.Entities.DTOs;
    ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
    builder.EntitySet<LocationDTO>("Locations");
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class LocationsController : ODataController
    {
        private static ODataValidationSettings _validationSettings = new ODataValidationSettings();
        private string _connectionString = "Server=localhost;Port=5012;UserId=postgres;Password=admin;Database=GeoAdDb";
        private ILocationsRepository _locationsRepository;

        public LocationsController() : base()
        {
            _locationsRepository = new PostgresqlLocationsRepository(_connectionString);
        }

        [Authorize]
        [EnableQuery]
        public IQueryable<LocationDTO> GetA()
        {
            return _locationsRepository.GetByUserId(RequestContext.Principal.Identity.GetHashCode()).AsQueryable();
        }

        // GET: odata/Locations
        public IHttpActionResult GetLocations(ODataQueryOptions<LocationDTO> queryOptions)
        {
            // validate the query.
            try
            {
                queryOptions.Validate(_validationSettings);
            }
            catch (ODataException ex)
            {
                return BadRequest(ex.Message);
            }

            // return Ok<IEnumerable<LocationDTO>>(locationDTOes);
            return StatusCode(HttpStatusCode.NotImplemented);
        }

        // GET: odata/Locations(5)
        public IHttpActionResult GetLocationDTO([FromODataUri] int key, ODataQueryOptions<LocationDTO> queryOptions)
        {
            // validate the query.
            try
            {
                queryOptions.Validate(_validationSettings);
            }
            catch (ODataException ex)
            {
                return BadRequest(ex.Message);
            }

            // return Ok<LocationDTO>(locationDTO);
            return StatusCode(HttpStatusCode.NotImplemented);
        }

        // PUT: odata/Locations(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<LocationDTO> delta)
        {
            Validate(delta.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // TODO: Get the entity here.

            // delta.Put(locationDTO);

            // TODO: Save the patched entity.

            // return Updated(locationDTO);
            return StatusCode(HttpStatusCode.NotImplemented);
        }

        // POST: odata/Locations
        public IHttpActionResult Post(LocationDTO locationDTO)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // TODO: Add create logic here.

            // return Created(locationDTO);
            return StatusCode(HttpStatusCode.NotImplemented);
        }

        // PATCH: odata/Locations(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<LocationDTO> delta)
        {
            Validate(delta.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // TODO: Get the entity here.

            // delta.Patch(locationDTO);

            // TODO: Save the patched entity.

            // return Updated(locationDTO);
            return StatusCode(HttpStatusCode.NotImplemented);
        }

        // DELETE: odata/Locations(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            // TODO: Add delete logic here.

            // return StatusCode(HttpStatusCode.NoContent);
            return StatusCode(HttpStatusCode.NotImplemented);
        }
    }
}
