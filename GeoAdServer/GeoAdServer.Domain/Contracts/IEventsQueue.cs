using GeoAdServer.Domain.Entities.Events;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.Serialization.Formatters.Binary;
using System.Text;
using System.Threading.Tasks;

namespace GeoAdServer.Domain.Contracts
{
    public interface IEventsQueue
    {
        void Enqueue(IEvent command);

        IEvent Dequeue();
    }

    public static class CommandsUtils
    {
        public static byte[] CommandToByteArray(IEvent command)
        {
            if (command == null)
                return null;

            BinaryFormatter bf = new BinaryFormatter();
            MemoryStream ms = new MemoryStream();
            bf.Serialize(ms, command);
            return ms.ToArray();
        }

        public static IEvent CommandFromByteArray(byte[] arrBytes)
        {
            MemoryStream memStream = new MemoryStream();
            BinaryFormatter binForm = new BinaryFormatter();
            memStream.Write(arrBytes, 0, arrBytes.Length);
            memStream.Seek(0, SeekOrigin.Begin);
            IEvent command = (IEvent)binForm.Deserialize(memStream);
            return command;
        }
    }
}
