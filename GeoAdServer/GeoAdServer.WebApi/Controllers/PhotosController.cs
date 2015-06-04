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
using GeoAdServer.WebApi.Models;
using System.Drawing;
using System.Drawing.Imaging;
using GeoAdServer.Domain.Contracts;

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

        [ActionName("Data")]
        public HttpResponseMessage GetPhotoData(int photoId)
        {
            PhotoDataDTO data = DataRepos.Photos.GetPhotoBase64Data(photoId);

            if (data != null)
            {
                HttpResponseMessage message = new HttpResponseMessage(HttpStatusCode.OK);
                message.Content = new StringContent(data.Base64Data);
                return message;
            } else return Request.CreateResponse(HttpStatusCode.NotFound);
        }

        [Authorize]
        public HttpResponseMessage Post([FromBody]PhotoApiModel photoApiModel)
        {
            using (IPhotosRepository repo = DataRepos.Photos)
            {
                if (photoApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                if (!photoApiModel.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                byte[] imageBytes = Convert.FromBase64String(photoApiModel.Base64Data);
                MemoryStream memoryStream = new MemoryStream(imageBytes, 0, imageBytes.Length);

                memoryStream.Write(imageBytes, 0, imageBytes.Length);
                Image imageFromUpload = Image.FromStream(memoryStream, true);

                Image imageToSave, thumb;

                if (imageFromUpload.Width > 1280 || imageFromUpload.Height > 960)
                {
                    imageToSave = (Image)(new Bitmap(imageFromUpload, new Size(1280, 960)));
                }
                else
                {
                    imageToSave = imageFromUpload;
                }

                if (imageFromUpload.Width > 200 || imageFromUpload.Height < 150)
                {
                    thumb = (Image)(new Bitmap(imageFromUpload, new Size(200, 150)));
                }
                else
                {
                    thumb = imageFromUpload;
                }

                string base64Thumbnail = "";
                string base64Data = "";

                using (MemoryStream ms = new MemoryStream())
                {
                    imageToSave.Save(ms, ImageFormat.Jpeg);
                    byte[] bytes = ms.ToArray();

                    base64Data = Convert.ToBase64String(bytes);
                }

                using (MemoryStream ms = new MemoryStream())
                {
                    thumb.Save(ms, ImageFormat.Jpeg);
                    byte[] bytes = ms.ToArray();

                    base64Thumbnail = Convert.ToBase64String(bytes);
                }

                var id = repo.Insert(new Photo
                {
                    LocationId = photoApiModel.LocationId,
                    Base64Thumbnail = base64Thumbnail,
                    Width = imageToSave.Width,
                    Height = imageToSave.Height
                });

                repo.InsertData(id, base64Data);

                return Request.CreateResponse(HttpStatusCode.OK, new PhotoDTOApiModel
                {
                    Id = id,
                    Base64Thumbnail = base64Thumbnail
                });
            }
           
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            using(IPhotosRepository repo = DataRepos.Photos)
            { 
                PhotoDTO photo = repo.GetById(id);

                if (!photo.LocationId.IsLocationOwner(RequestContext.GetUserId()))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                var result = repo.Delete(id);
                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
            }
        }
    }
}
