using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface ILocationsRepository
    {
        IEnumerable<LocationDTO> GetAll();

        IEnumerable<LocationDTO> GetByUserId(int userId);

        LocationDTO GetById(int locationId);

        int Insert(Location location);

        void Update(int id, Location location);

        bool DeleteById(int locationId);

        bool InsertCategory(string name);

        bool DeleteCategory(string name);

        Dictionary<int, string> GetCategories();
    }
}
