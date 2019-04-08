package pl.jsql.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.dto.request.BuildsRequest
import pl.jsql.api.dto.request.QueriesRequest
import pl.jsql.api.dto.request.RequestsRequest
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.Query
import pl.jsql.api.model.stats.Build
import pl.jsql.api.model.stats.Request
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.ApplicationDao
import pl.jsql.api.repo.BuildDao
import pl.jsql.api.repo.QueryDao
import pl.jsql.api.repo.RequestDao
import pl.jsql.api.security.service.SecurityService

import java.text.SimpleDateFormat

import static pl.jsql.api.enums.HttpMessageEnum.SUCCESS

@Transactional
@Service
public class  StatsService {

    @Autowired
    BuildDao buildDao

    @Autowired
    RequestDao requestDao

    @Autowired
    SecurityService securityService

    @Autowired
    QueryDao queryDao

    @Autowired
    ApplicationDao applicationDao

    @Async
    void saveBuild(Application application, User user, Integer queriesCount) {

        Build build = new Build()
        build.application = application
        build.user = user
        build.hashingDate = new Date()
        build.queriesCount = queriesCount
        buildDao.save(build)

    }


    @Async
    void saveRequest(Application application, User user, String queryHash) {

        Request request = new Request()
        request.application = application
        request.user = user
        request.queryHash = queryHash
        request.requestDate = new Date()

        requestDao.save(request)

    }

    Date currentDate() {
        Calendar cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTime()
    }

    def getBuildsByUser(BuildsRequest buildRequest) {
        SimpleDateFormat simplify = new SimpleDateFormat("HH:mm:ss dd-MM-YYY")
        User user = securityService.getCurrentAccount()
        if (buildRequest.applications == null || buildRequest.applications == []){
            return [code: SUCCESS.getCode(), data: ""]
        }
        List<Build> builds = buildDao.findByCompanyAndCreatedDateBetween(buildRequest.dateFrom, buildRequest.dateTo, user.company, buildRequest.applications, buildRequest.members)

        def list = []
        List buildDatesList = builds.hashingDate
        int buildsTotal = countMatches(buildDatesList, buildRequest.dateFrom, buildRequest.dateTo)
        int buildsToday = countMatches(buildDatesList, currentDate(), new Date())

        list << [
                buildsTotal: buildsTotal,
                buildsToday: buildsToday
        ]

        def buildList = []
        for (Build build : builds) {
            String simpleData = simplify.format(build.hashingDate)
            buildList << [
                    applicationName: build.application.name,
                    buildOwner     : build.user.firstName + " " + build.user.lastName,
                    hashingTime    : simpleData,
                    apiId          : build.application.id
            ]
        }
        list << [
                buildList: buildList
        ]


        return [code: SUCCESS.getCode(), data: list]
    }


    int countMatches(ArrayList<Date> list, Date startDate, Date stopDate) {
        int countedMatches = 0
        for (Date date : list) {
            if (date.after(startDate) && date.before(stopDate)) {
                countedMatches++
            }
        }
        return countedMatches
    }

    def getQueries(QueriesRequest queriesRequest) {

        User currentUser = securityService.getCurrentAccount()

        List<Query> queries = queryDao.findByCompanyAndCreatedDateBetween(queriesRequest.dateFrom,
                queriesRequest.dateTo, currentUser.company, queriesRequest.applications, queriesRequest.members, queriesRequest.used, queriesRequest.dynamic)

        def data = []

        queries.each { query ->
            data << [
                    id       : query.id,
                    query    : query.query,
                    hash     : query.hash,
                    queryDate: new SimpleDateFormat('yyyy-MM-dd HH:mm').format(query.queryDate),
                    used     : query.used,
                    dynamic  : query.dynamic,
                    user     : query.user.email
            ]
        }

        return [code: SUCCESS.getCode(), data: data]
    }

    def getRequests(RequestsRequest request) {
        User currentUser = securityService.getCurrentAccount()
        def requests = requestDao.findByApplicationsAndDateBetween(request.dateFrom, request.dateTo, currentUser.company, request.applications)
        def data = []

        requests.each { req ->
            data << [
                    application  : req.application,
                    totalRequests: req.totalRequests,
                    date         : new SimpleDateFormat('dd-MM-YYYY').format(getDateByDayMonthAndYear(req.year, req.month, req.day)),
                    requests     : getRequestsByHour(req)
            ]
        }

        return [code: SUCCESS.getCode(), data: data]
    }

    Date getDateByDayMonthAndYear(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).getTime()
    }

    def getRequestsByHour(def req) {

        List<List<Long>> counts = requestDao.countByHour(applicationDao.findById(req.application).get(), req.day, req.month, req.year)
        Map<String, Long> resp = new LinkedHashMap<>()

        for (List<Long> list : counts) {
            String hour = list.get(1).toString().length() > 1 ? list.get(1).toString() : "0" + list.get(1).toString()
            resp.put(hour + ":00", list.get(0))
        }

        return resp
    }

}
