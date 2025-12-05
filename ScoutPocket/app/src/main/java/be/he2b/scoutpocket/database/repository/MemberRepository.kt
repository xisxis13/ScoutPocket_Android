package be.he2b.scoutpocket.database.repository

import android.content.Context
import be.he2b.scoutpocket.database.ScoutPocketDatabase
import be.he2b.scoutpocket.database.entity.Member
import be.he2b.scoutpocket.model.Section

class MemberRepository(context: Context) {

    private val db = ScoutPocketDatabase.Companion.getInstance(context)
    private val memberDao = db.memberDao()

    suspend fun addMember(member: Member) = memberDao.insert(member)
    suspend fun getAllMembers(): List<Member> = memberDao.getAllMembers()
    suspend fun getMembersBySection(section: Section): List<Member> = memberDao.getMembersBySection(section)
    suspend fun getMemberById(memberId: Int): Member? = memberDao.getMemberById(memberId)

}