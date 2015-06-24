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

        IEnumerable<LocationDTO> GetAllAround(double lat, double lng, double radius);

        IEnumerable<LocationDTO> GetByUserId(string userId);

        IEnumerable<LocationDTO> GetByIds(IEnumerable<int> ids);

        string GetOwnerId(int locationId);

        LocationDTO GetById(int locationId);

        int Insert(Location location);

        bool Update(int id, Location location);

        bool DeleteById(int locationId);

        int InsertCategory(string name, int? aggregate);

        bool DeleteCategory(string name);

        Dictionary<string, IList<string>> GetCategories();

        CategoryDTO GetCategoryById(int id);

        CategoryDTO GetCategoryByName(string name);
    }
}
