using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.WebApi.Models
{
    public class UserFavsIgn
    {
        public List<LocationDTO> Favorites { get; set; }

        public List<IgnoredLocation> Ignored { get; set; }
    }

    public class IgnoredLocation
    {
        public int Id { get; set; }

        public string Name { get; set; }
    }
}
