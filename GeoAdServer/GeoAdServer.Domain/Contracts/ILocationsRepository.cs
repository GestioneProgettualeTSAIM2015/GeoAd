using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface ILocationsRepository : IDisposable
    {
        IEnumerable<LocationDTO> GetAll();

        IEnumerable<LocationDTO> GetByUserId(string userId);

        string GetOwnerId(int locationId);

        LocationDTO GetById(int locationId);

        int Insert(Location location);

        bool Update(int id, Location location);

        bool DeleteById(int locationId);

        int InsertCategory(string name);

        bool DeleteCategory(string name);

        Dictionary<int, string> GetCategories();
    }
}
