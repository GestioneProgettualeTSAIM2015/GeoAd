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
        private static double IMAGE_WIDTH = 960.0;
        private static double IMAGE_HEIGHT = 720.0;
        private static double THUMB_WIDTH = 200.0;
        private static double THUMB_HEIGHT = 150.0;

        [ActionName("FromLocation")]
        public IQueryable<PhotoDTO> GetFromLocation(int locationId)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Photos.GetByLocationId(locationId).AsQueryable();
            }
        }

        public PhotoDTO Get(int id)
        {
            using (var repos = DataRepos.Instance)
            {
                return repos.Photos.GetById(id);
            }
        }

        [Authorize]
        [ActionName("Data")]
        public HttpResponseMessage GetData(int photoId)
        {
            using (var repos = DataRepos.Instance)
            {
                PhotoDataDTO data = repos.Photos.GetPhotoBase64Data(photoId);

                if (data != null)
                {
                    HttpResponseMessage message = new HttpResponseMessage(HttpStatusCode.OK);
                    message.Content = new StringContent(data.Base64Data);
                    return message;
                }
                else return Request.CreateResponse(HttpStatusCode.NotFound);
            }
        }

        [Authorize]
        public HttpResponseMessage Post([FromBody]PhotoApiModel photoApiModel)
        {
            using (var repos = DataRepos.Instance)
            {
                if (photoApiModel == null) return Request.CreateResponse(HttpStatusCode.BadRequest);

                if (!photoApiModel.LocationId.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                byte[] imageBytes = Convert.FromBase64String(photoApiModel.Base64Data);
                MemoryStream memoryStream = new MemoryStream(imageBytes, 0, imageBytes.Length);

                memoryStream.Write(imageBytes, 0, imageBytes.Length);
                Image imageFromUpload = Image.FromStream(memoryStream, true);

                Image imageToSave, thumb;

                if (imageFromUpload.Width > IMAGE_WIDTH || imageFromUpload.Height > IMAGE_HEIGHT)
                {
                    var widthScale = (double)imageFromUpload.Width / IMAGE_WIDTH;
                    var heightScale = (double)imageFromUpload.Height / IMAGE_HEIGHT;
                    var scale = widthScale > heightScale ? widthScale : heightScale;
                    
                    imageToSave = (Image)(new Bitmap(imageFromUpload, new Size((int)(imageFromUpload.Width / scale), (int) (imageFromUpload.Height / scale))));
                }
                else
                {
                    imageToSave = imageFromUpload;
                }

                if (imageFromUpload.Width > THUMB_WIDTH || imageFromUpload.Height > THUMB_WIDTH)
                {
                    var widthScale = (double)imageFromUpload.Width / THUMB_WIDTH;
                    var heightScale = (double)imageFromUpload.Height / THUMB_HEIGHT;
                    var scale = widthScale > heightScale ? widthScale : heightScale;

                    thumb = (Image)(new Bitmap(imageFromUpload, new Size((int)(imageFromUpload.Width / scale), (int)(imageFromUpload.Height / scale))));
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

                var id = repos.Photos.Insert(new Photo
                {
                    LocationId = photoApiModel.LocationId,
                    Base64Thumbnail = base64Thumbnail,
                    Width = imageToSave.Width,
                    Height = imageToSave.Height
                });

                repos.Photos.InsertData(id, base64Data);

                return Request.CreateResponse(HttpStatusCode.OK, new PhotoDTOApiModel
                {
                    Id = id,
                    Base64Thumbnail = base64Thumbnail,
                    WidthBig = imageToSave.Width,
                    HeightBig = imageToSave.Height
                });
            }
           
        }

        [Authorize]
        public HttpResponseMessage Delete(int id)
        {
            using (var repos = DataRepos.Instance)
            { 
                PhotoDTO photo = repos.Photos.GetById(id);

                if (!photo.LocationId.IsLocationOwner(RequestContext.GetUserId(), repos.Locations))
                    return Request.CreateResponse(HttpStatusCode.Unauthorized);

                var result = repos.Photos.Delete(id);
                return Request.CreateResponse(result ? HttpStatusCode.NoContent : HttpStatusCode.NotFound);
            }
        }
    }
}
