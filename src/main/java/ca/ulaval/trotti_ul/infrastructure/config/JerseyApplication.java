package ca.ulaval.trotti_ul.infrastructure.config;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Properties;

import javax.crypto.SecretKey;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import ca.ulaval.trotti_ul.api.account.AccountBalanceResource;
import ca.ulaval.trotti_ul.api.account.AccountInvoiceResource;
import ca.ulaval.trotti_ul.api.auth.AuthResource;
import ca.ulaval.trotti_ul.api.common.ForbiddenExceptionMapper;
import ca.ulaval.trotti_ul.api.maintenance.MaintenanceResource;
import ca.ulaval.trotti_ul.api.maintenance.TechnicianTruckResource;
import ca.ulaval.trotti_ul.api.pass.PassResource;
import ca.ulaval.trotti_ul.api.payment.PaymentResource;
import ca.ulaval.trotti_ul.api.ride.RideResource;
import ca.ulaval.trotti_ul.api.security.JwtAuthFilter;
import ca.ulaval.trotti_ul.api.semester.SemesterResource;
import ca.ulaval.trotti_ul.api.station.StationResource;
import ca.ulaval.trotti_ul.api.technician.TechnicianResource;
import ca.ulaval.trotti_ul.application.account.CreateAccountUseCase;
import ca.ulaval.trotti_ul.application.auth.LoginUseCase;
import ca.ulaval.trotti_ul.application.auth.TokenService;
import ca.ulaval.trotti_ul.application.billing.GetAccountBalanceUseCase;
import ca.ulaval.trotti_ul.application.billing.GetAccountInvoicesUseCase;
import ca.ulaval.trotti_ul.application.maintenance.EndMaintenanceUseCase;
import ca.ulaval.trotti_ul.application.maintenance.GetMaintenanceRequestsUseCase;
import ca.ulaval.trotti_ul.application.maintenance.GetMaintenanceStatusUseCase;
import ca.ulaval.trotti_ul.application.maintenance.GetTruckContentsUseCase;
import ca.ulaval.trotti_ul.application.maintenance.LoadScootersToTruckUseCase;
import ca.ulaval.trotti_ul.application.maintenance.RequestMaintenanceUseCase;
import ca.ulaval.trotti_ul.application.maintenance.StartMaintenanceUseCase;
import ca.ulaval.trotti_ul.application.maintenance.UnloadScootersFromTruckUseCase;
import ca.ulaval.trotti_ul.application.pass.GetAccountPassesUseCase;
import ca.ulaval.trotti_ul.application.pass.GetValidPassUseCase;
import ca.ulaval.trotti_ul.application.pass.PurchasePassUseCase;
import ca.ulaval.trotti_ul.application.payment.AddCreditCardUseCase;
import ca.ulaval.trotti_ul.application.ride.EndRideUseCase;
import ca.ulaval.trotti_ul.application.ride.GenerateCodeUseCase;
import ca.ulaval.trotti_ul.application.ride.GetRideHistoryUseCase;
import ca.ulaval.trotti_ul.application.ride.StartRideUseCase;
import ca.ulaval.trotti_ul.application.station.GetStationDetailsUseCase;
import ca.ulaval.trotti_ul.application.station.GetStationsUseCase;
import ca.ulaval.trotti_ul.application.technician.CreateTechnicianUseCase;
import ca.ulaval.trotti_ul.domain.account.AccountRepository;
import ca.ulaval.trotti_ul.domain.billing.InvoiceRepository;
import ca.ulaval.trotti_ul.domain.billing.RideBillingService;
import ca.ulaval.trotti_ul.domain.employee.EmployeeCatalog;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceDomainService;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRepository;
import ca.ulaval.trotti_ul.domain.maintenance.MaintenanceRequestRepository;
import ca.ulaval.trotti_ul.domain.maintenance.TechnicianTruckRepository;
import ca.ulaval.trotti_ul.domain.notification.EmailSender;
import ca.ulaval.trotti_ul.domain.notification.TemplatedEmailService;
import ca.ulaval.trotti_ul.domain.pass.AccessPlanService;
import ca.ulaval.trotti_ul.domain.pass.PassRepository;
import ca.ulaval.trotti_ul.domain.payment.CreditCardRepository;
import ca.ulaval.trotti_ul.domain.payment.PaymentGateway;
import ca.ulaval.trotti_ul.domain.ride.RideCodeRepository;
import ca.ulaval.trotti_ul.domain.ride.RidePolicy;
import ca.ulaval.trotti_ul.domain.ride.RideRepository;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyRepository;
import ca.ulaval.trotti_ul.domain.scooter.ScooterEnergyService;
import ca.ulaval.trotti_ul.domain.scooter.ScooterInventory;
import ca.ulaval.trotti_ul.domain.scooter.ScooterReservation;
import ca.ulaval.trotti_ul.domain.security.PasswordHasher;
import ca.ulaval.trotti_ul.domain.semester.SemesterCatalog;
import ca.ulaval.trotti_ul.domain.station.StationRepository;
import ca.ulaval.trotti_ul.domain.technician.TechnicianRepository;
import ca.ulaval.trotti_ul.infrastructure.account.InMemoryAccountRepository;
import ca.ulaval.trotti_ul.infrastructure.billing.InMemoryInvoiceRepository;
import ca.ulaval.trotti_ul.infrastructure.billing.RideBillingAdapter;
import ca.ulaval.trotti_ul.infrastructure.employee.CsvEmployeeCatalog;
import ca.ulaval.trotti_ul.infrastructure.maintenance.InMemoryMaintenanceRepository;
import ca.ulaval.trotti_ul.infrastructure.maintenance.InMemoryMaintenanceRequestRepository;
import ca.ulaval.trotti_ul.infrastructure.maintenance.InMemoryTechnicianTruckRepository;
import ca.ulaval.trotti_ul.infrastructure.notification.ClasspathTemplatedEmailService;
import ca.ulaval.trotti_ul.infrastructure.notification.EmailTemplates;
import ca.ulaval.trotti_ul.infrastructure.notification.SmtpEmailSender;
import ca.ulaval.trotti_ul.infrastructure.pass.InMemoryPassRepository;
import ca.ulaval.trotti_ul.infrastructure.payment.FakePaymentGateway;
import ca.ulaval.trotti_ul.infrastructure.payment.InMemoryCreditCardRepository;
import ca.ulaval.trotti_ul.infrastructure.ride.InMemoryRideCodeRepository;
import ca.ulaval.trotti_ul.infrastructure.ride.InMemoryRideRepository;
import ca.ulaval.trotti_ul.infrastructure.scooter.InMemoryScooterRepository;
import ca.ulaval.trotti_ul.infrastructure.security.SimplePasswordHasher;
import ca.ulaval.trotti_ul.infrastructure.security.jwt.JwtDecoder;
import ca.ulaval.trotti_ul.infrastructure.security.jwt.JwtEncoder;
import ca.ulaval.trotti_ul.infrastructure.security.jwt.JwtTokenServiceAdapter;
import ca.ulaval.trotti_ul.infrastructure.security.jwt.SimpleJwtDecoder;
import ca.ulaval.trotti_ul.infrastructure.security.jwt.SimpleJwtEncoder;
import ca.ulaval.trotti_ul.infrastructure.semester.JsonSemesterCatalog;
import ca.ulaval.trotti_ul.infrastructure.station.InMemoryStationRepository;
import ca.ulaval.trotti_ul.infrastructure.technician.InMemoryTechnicianRepository;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        packages("ca.ulaval.trotti_ul.api.security",
                 "ca.ulaval.trotti_ul.api.common");

        register(JacksonJsonProvider.class);
        register(ObjectMapperProvider.class);

        configureDependencies();
    }

    private void configureDependencies() {
        Clock clock = Clock.systemDefaultZone();

        AccountRepository accountRepository = new InMemoryAccountRepository();
        TechnicianRepository technicianRepository = new InMemoryTechnicianRepository();
        PassRepository passRepository = new InMemoryPassRepository();
        CreditCardRepository creditCardRepository = new InMemoryCreditCardRepository();
        InvoiceRepository invoiceRepository = new InMemoryInvoiceRepository();
        EmailSender emailSender = new SmtpEmailSender();
        TemplatedEmailService emailService = new ClasspathTemplatedEmailService(emailSender, EmailTemplates.defaultCatalog());
        RideCodeRepository rideCodeRepository = new InMemoryRideCodeRepository();
        RideRepository rideRepository = new InMemoryRideRepository();
        InMemoryStationRepository stationRepository = new InMemoryStationRepository();
        InMemoryScooterRepository scooterRepository = new InMemoryScooterRepository();

        MaintenanceRequestRepository maintenanceRequestRepository = new InMemoryMaintenanceRequestRepository();
        MaintenanceRepository maintenanceRepository = new InMemoryMaintenanceRepository();
        TechnicianTruckRepository technicianTruckRepository = new InMemoryTechnicianTruckRepository();
        ScooterEnergyService scooterEnergyService = new ScooterEnergyService(scooterRepository, maintenanceRepository, clock);
        MaintenanceDomainService maintenanceDomainService = new MaintenanceDomainService(maintenanceRepository, maintenanceRequestRepository, stationRepository, technicianRepository, technicianTruckRepository, clock);

        EmployeeCatalog employeeCatalog = new CsvEmployeeCatalog("employes.csv");
        SemesterCatalog semesterCatalog = new JsonSemesterCatalog();
        PaymentGateway paymentGateway = new FakePaymentGateway();
        PasswordHasher passwordHasher = new SimplePasswordHasher();
        TokenService tokenService = createTokenService();

        RideBillingService rideBillingService = new RideBillingAdapter(invoiceRepository, creditCardRepository, paymentGateway, clock);
        AccessPlanService accessPlanService = new AccessPlanService(semesterCatalog, passRepository);
        RidePolicy ridePolicy = new RidePolicy(clock);


        CreateAccountUseCase createAccountUseCase = new CreateAccountUseCase(
                accountRepository,
                employeeCatalog,
                passwordHasher,
                clock
        );

        LoginUseCase loginUseCase = new LoginUseCase(
                accountRepository,
                technicianRepository,
                passwordHasher,
                tokenService,
                clock
        );

        CreateTechnicianUseCase createTechnicianUseCase = new CreateTechnicianUseCase(
                accountRepository,
                technicianRepository,
                employeeCatalog,
                passwordHasher,
                clock
        );

        PurchasePassUseCase purchasePassUseCase = new PurchasePassUseCase(
                semesterCatalog,
                passRepository,
                creditCardRepository,
                paymentGateway,
                invoiceRepository,
                accountRepository,
                emailService,
                clock
        );

        GetAccountPassesUseCase getAccountPassesUseCase = new GetAccountPassesUseCase(passRepository);

        GetValidPassUseCase getValidPassUseCase = new GetValidPassUseCase(
                passRepository,
                semesterCatalog,
                clock
        );

        GenerateCodeUseCase generateCodeUseCase = new GenerateCodeUseCase(
                accessPlanService,
                rideCodeRepository,
                accountRepository,
                emailService,
                clock
        );

        StartRideUseCase startRideUseCase = new StartRideUseCase(
                accessPlanService,
                rideCodeRepository,
                rideRepository,
                scooterRepository,
                scooterEnergyService,
                stationRepository,
                accountRepository,
                clock
        );

        EndRideUseCase endRideUseCase = new EndRideUseCase(
                passRepository,
                accessPlanService,
                rideRepository,
                scooterRepository,
                scooterEnergyService,
                stationRepository,
                accountRepository,
                rideBillingService,
                ridePolicy,
                clock
        );

        GetRideHistoryUseCase getRideHistoryUseCase = new GetRideHistoryUseCase(rideRepository);
        GetAccountBalanceUseCase getAccountBalanceUseCase = new GetAccountBalanceUseCase(invoiceRepository);
        GetAccountInvoicesUseCase getAccountInvoicesUseCase = new GetAccountInvoicesUseCase(invoiceRepository);
        GetStationsUseCase getStationsUseCase = new GetStationsUseCase(stationRepository, scooterRepository, scooterEnergyService);
        GetStationDetailsUseCase getStationDetailsUseCase = new GetStationDetailsUseCase(stationRepository, scooterRepository, scooterEnergyService);

        AddCreditCardUseCase addCreditCardUseCase = new AddCreditCardUseCase(creditCardRepository);

        RequestMaintenanceUseCase requestMaintenanceUseCase = new RequestMaintenanceUseCase(
                maintenanceRequestRepository,
                stationRepository,
                technicianRepository,
                accountRepository,
                emailService,
                clock
        );

        GetMaintenanceRequestsUseCase getMaintenanceRequestsUseCase = new GetMaintenanceRequestsUseCase(
                maintenanceRequestRepository
        );

        StartMaintenanceUseCase startMaintenanceUseCase = new StartMaintenanceUseCase(
                maintenanceRepository,
                maintenanceRequestRepository,
                stationRepository,
                technicianRepository,
                clock
        );

        EndMaintenanceUseCase endMaintenanceUseCase = new EndMaintenanceUseCase(
                maintenanceRepository,
                maintenanceRequestRepository,
                stationRepository,
                technicianRepository,
                technicianTruckRepository,
                maintenanceDomainService,
                clock
        );

        LoadScootersToTruckUseCase loadScootersToTruckUseCase = new LoadScootersToTruckUseCase(
                technicianRepository,
                technicianTruckRepository,
                stationRepository,
                scooterRepository,
                scooterEnergyService
        );

        UnloadScootersFromTruckUseCase unloadScootersFromTruckUseCase = new UnloadScootersFromTruckUseCase(
                technicianRepository,
                technicianTruckRepository,
                stationRepository,
                scooterRepository,
                scooterRepository,
                scooterEnergyService
        );

        GetMaintenanceStatusUseCase getMaintenanceStatusUseCase = new GetMaintenanceStatusUseCase(
                maintenanceRepository,
                stationRepository
        );

        GetTruckContentsUseCase getTruckContentsUseCase = new GetTruckContentsUseCase(
                technicianRepository,
                technicianTruckRepository
        );

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(tokenService);
        register(jwtAuthFilter);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(createAccountUseCase).to(CreateAccountUseCase.class);
                bind(loginUseCase).to(LoginUseCase.class);
                bind(createTechnicianUseCase).to(CreateTechnicianUseCase.class);

                bind(purchasePassUseCase).to(PurchasePassUseCase.class);
                bind(getAccountPassesUseCase).to(GetAccountPassesUseCase.class);
                bind(getValidPassUseCase).to(GetValidPassUseCase.class);
                bind(generateCodeUseCase).to(GenerateCodeUseCase.class);
                bind(startRideUseCase).to(StartRideUseCase.class);
                bind(endRideUseCase).to(EndRideUseCase.class);
                bind(getRideHistoryUseCase).to(GetRideHistoryUseCase.class);
                bind(getAccountBalanceUseCase).to(GetAccountBalanceUseCase.class);
                bind(getAccountInvoicesUseCase).to(GetAccountInvoicesUseCase.class);
                bind(getStationsUseCase).to(GetStationsUseCase.class);
                bind(getStationDetailsUseCase).to(GetStationDetailsUseCase.class);
                bind(accessPlanService).to(AccessPlanService.class);

                bind(addCreditCardUseCase).to(AddCreditCardUseCase.class);

                bind(requestMaintenanceUseCase).to(RequestMaintenanceUseCase.class);
                bind(getMaintenanceRequestsUseCase).to(GetMaintenanceRequestsUseCase.class);
                bind(startMaintenanceUseCase).to(StartMaintenanceUseCase.class);
                bind(endMaintenanceUseCase).to(EndMaintenanceUseCase.class);
                bind(loadScootersToTruckUseCase).to(LoadScootersToTruckUseCase.class);
                bind(unloadScootersFromTruckUseCase).to(UnloadScootersFromTruckUseCase.class);
                bind(getMaintenanceStatusUseCase).to(GetMaintenanceStatusUseCase.class);
                bind(getTruckContentsUseCase).to(GetTruckContentsUseCase.class);

                bind(accountRepository).to(AccountRepository.class);
                bind(technicianRepository).to(TechnicianRepository.class);
                bind(passRepository).to(PassRepository.class);
                bind(creditCardRepository).to(CreditCardRepository.class);
                bind(invoiceRepository).to(InvoiceRepository.class);
                bind(rideBillingService).to(RideBillingService.class);
                bind(tokenService).to(TokenService.class);
                bind(emailSender).to(EmailSender.class);
                bind(emailService).to(TemplatedEmailService.class);
                bind(rideCodeRepository).to(RideCodeRepository.class);
                bind(rideRepository).to(RideRepository.class);
                bind(stationRepository).to(StationRepository.class);
                bind(scooterRepository).to(ScooterReservation.class);
                bind(scooterRepository).to(ScooterInventory.class);
                bind(scooterRepository).to(ScooterEnergyRepository.class);
                bind(scooterEnergyService).to(ScooterEnergyService.class);
                bind(maintenanceRequestRepository).to(MaintenanceRequestRepository.class);
                bind(maintenanceRepository).to(MaintenanceRepository.class);
                bind(technicianTruckRepository).to(TechnicianTruckRepository.class);
                bind(maintenanceDomainService).to(MaintenanceDomainService.class);

                bind(employeeCatalog).to(EmployeeCatalog.class);
                bind(semesterCatalog).to(SemesterCatalog.class);

                bind(clock).to(Clock.class);
            }
        });

        register(AuthResource.class);
        register(SemesterResource.class);
        register(PassResource.class);
        register(PaymentResource.class);
        register(RideResource.class);
        register(StationResource.class);
        register(AccountBalanceResource.class);
        register(AccountInvoiceResource.class);
        register(MaintenanceResource.class);
        register(TechnicianTruckResource.class);
        register(TechnicianResource.class);
        register(RolesAllowedDynamicFeature.class);
        register(ForbiddenExceptionMapper.class);
    }

    private Properties loadApplicationProperties() {
        Properties props = new Properties();
        try (var in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
        }
        return props;
    }

    private String resolveProperty(String key, Properties props) {
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }
        String fromFile = props.getProperty(key);
        if (fromFile == null || fromFile.isBlank()) {
            throw new IllegalStateException("Missing required configuration property: " + key);
        }
        return fromFile;
    }

    private TokenService createTokenService() {
        Properties appProps = loadApplicationProperties();
        String jwtSecret = resolveProperty("JWT_SECRET", appProps);
        SecretKey jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        JwtEncoder jwtEncoder = new SimpleJwtEncoder(jwtSecretKey);
        JwtDecoder jwtDecoder = new SimpleJwtDecoder(jwtSecretKey);
        return new JwtTokenServiceAdapter(jwtEncoder, jwtDecoder);
    }
}
