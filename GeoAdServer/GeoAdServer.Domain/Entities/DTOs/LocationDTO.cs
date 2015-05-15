using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Entities.DTOs
{
    public class LocationDTO : ReflectiveEquals
    {
        public int Id { get; set; }

        public string PCat { get; set; }

        public string SCat { get; set; }

        public string Name { get; set; }

        public string Lat { get; set; }

        public string Lng { get; set; }

        public string Desc { get; set; }

        public string Type { get; set; }

        /*public override bool Equals(object obj)
        {
            if (!(obj is LocationDTO)) return false;

            LocationDTO ldto = obj as LocationDTO;

            return Id == ldto.Id &&
                   PCat == ldto.PCat &&
                   SCat == ldto.SCat &&
                   Name == ldto.Name &&
                   Lat == ldto.Lat &&
                   Lng == ldto.Lng &&
                   Desc == ldto.Desc &&
                   Type == ldto.Type;
        }*/
    }
}
