using GeoAdServer.Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface ILocationsRepository
    {
        IEnumerable<Location> GetAll();

        IEnumerable<Location> GetByUserId(int userId);

        Location GetById(int locationId);

        void Insert(Location location);

        void Update(Location location);

        bool DeleteById(int locationId);
    }
}
