using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IChangedPositionHandler
    {
        void Handle(ChangedPosition chp);

        IEnumerable<string> GetKeysAffected(double lat, double lng);
    }
}
