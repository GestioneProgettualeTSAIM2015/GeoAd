using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace GeoAdServer.WebApi.Models
{
    public class PhotoDTOApiModel
    {
        public int Id { get; set; }

        public string Base64Thumbnail { get; set; }
    }
}