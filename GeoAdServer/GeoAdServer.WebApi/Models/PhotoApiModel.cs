using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace GeoAdServer.WebApi.Models
{
    public class PhotoApiModel
    {
        public int LocationId { get; set; }

        public string Base64Data { get; set; }
    }
}