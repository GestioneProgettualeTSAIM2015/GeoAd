using GeoAdServer.Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IOfferingsRepository
    {
        IEnumerable<Offering> GetAll();

        IEnumerable<Offering> GetByLocationId(int locationId);

        IEnumerable<Offering> GetByTypeId(int typeId);

        void Insert(Offering offering);

        void Update(Offering offering);

        bool DeleteById(int offeringId);
    }
}
