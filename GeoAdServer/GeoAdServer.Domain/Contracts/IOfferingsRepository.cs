using GeoAdServer.Domain.Entities;
using GeoAdServer.Domain.Entities.DTOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IOfferingsRepository
    {
        IEnumerable<OfferingDTO> GetAll();

        IEnumerable<OfferingDTO> GetByLocationId(int locationId);

        int Insert(Offering offering);

        bool Update(int id, Offering offering);

        bool DeleteById(int offeringId);
    }
}
