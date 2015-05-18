using GeoAdServer.DataAccess;
using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Web;
using System.Web.Http;
using GeoAdServer.WebApi.Support;

namespace GeoAdServer.WebApi.Controllers
{
    public class PhotosController : ApiController
    {
        [ActionName("FromLocation")]
        public IQueryable<PhotoDTO> GetFromLocation(int locationId)
        {
            return DataRepos.Photos.GetByLocationId(locationId).AsQueryable();
        }

        public PhotoDTO Get(int id)
        {
            return DataRepos.Photos.GetById(id);
        }

        public HttpResponseMessage GetPhotoData(int id)
        {
            byte[] data = DataRepos.Photos.GetPhotoData(id);
            if (data != null)
            {
                HttpResponseMessage message = new HttpResponseMessage(HttpStatusCode.OK);
                message.Content = new ByteArrayContent(data);
                message.Content.Headers.ContentType = new MediaTypeHeaderValue("application/octet-stream");
                return message;
            } else return Request.CreateResponse(HttpStatusCode.NotFound);
        }

        [Authorize]
        public HttpResponseMessage Post([FromBody]Photo photo)
        {
            if (photo == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

            if (!photo.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                return Request.CreateResponse(HttpStatusCode.Unauthorized);

            if (HttpContext.Current.Request.Files.AllKeys.Any())
            {
                var dataFile = HttpContext.Current.Request.Files["Data"];

                if (dataFile != null)
                {
                    var stream = dataFile.InputStream;
                    byte[] fileData = null;
                    using (var binaryReader = new BinaryReader(stream))
                    {
                        fileData = binaryReader.ReadBytes(dataFile.ContentLength);
                    }
                    photo.Data = fileData;

                    var id = DataRepos.Photos.Insert(photo);
                    return Request.CreateResponse(HttpStatusCode.OK, id);
                }
                else return Request.CreateResponse(HttpStatusCode.BadRequest);
            }
            else return Request.CreateResponse(HttpStatusCode.BadRequest);
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            if (!id.IsLocationOwner(RequestContext.GetUserId()))
                return Request.CreateResponse(HttpStatusCode.Unauthorized);

            var result = DataRepos.Photos.Delete(id);
            return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
        }
    }
}
