using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace GeoAdServer.WebApi.Models
{
    public class PhotoApiModel
    {
        [Required]
        public int LocationId { get; set; }

        [Required]
        public string Base64Data { get; set; }
    }
}