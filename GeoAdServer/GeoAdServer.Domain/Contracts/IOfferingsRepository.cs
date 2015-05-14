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

        IEnumerable<OfferingDTO> GetByTypeId(int typeId);

        void Insert(Offering offering);

        void Update(int id, Offering offering);

        bool DeleteById(int offeringId);
    }
}
