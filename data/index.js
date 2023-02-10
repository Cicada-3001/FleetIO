import mongoose from "mongoose";

const vehicleIds = [
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
  new mongoose.Types.ObjectId(),
];


const driverIds = [
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
    new mongoose.Types.ObjectId(),
];



export const vehicles = [
  {
    _id: vehicleIds[0],
    vehicleType: "dummy type",
    plateNumber: "dummy plate",
    make: "dummy make",
    model: "dummy model",
    year: "dummy year",
    vin: 1234,
    fuelType: "dummy fuel Type",
    odometerReading: 1334,
    currentLocation: "Narobi",
    availability: true
  }, 
];



export const drivers = [
  {
    _id: driverIds[0],
    firstName:  "dummy firstname", 
    lastName:  "dummy lastname", 
    licenseNumber: "dummy licenseNumber", 
    dateOfBirth: "dummy date of birth", 
    phoneNumber: "dummy phone number" ,
    email: "dummy email", 
    vehicle: vehicleIds[0]
  },
];


export const maintenances = [
    {
      _id: new mongoose.Types.ObjectId(),
      vehicle:vehicleIds[0], 
      maintenanceType: "dummy maintenance type", 
      date: "dummy date", 
      cost: 113000,
      description: "dummy description", 

    },
  ];


export const trips = [
    {
      _id: new mongoose.Types.ObjectId(),
      vehicle: vehicleIds[0], 
      driver: driverIds[0], 
      startTime: "dummy start time", 
      endTime: "dummy end time", 
      startLocation: "dummy start location",
      endLocation: "dummy  end location", 
      purpose: "dummy purpose", 
      distanceTravelled: 11, 
      fuelUsed: "dummy fuel used",
    },
];


export const fuels = [
    {
      _id: new mongoose.Types.ObjectId(),
      vehicle: vehicleIds[0],
      fuelType: "dummy fuel Type", 
      Location: "dummy location",
      cost:  "dummy cost ",
      gallons: 600
    },
];

